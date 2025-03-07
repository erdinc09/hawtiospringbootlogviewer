import {PageSection} from "@patternfly/react-core";

import {
    LogViewer as PatternFlyLogViewer,
    LogViewerSearch,
} from "@patternfly/react-log-viewer";
import {
    Button,
    Tooltip,
    Toolbar,
    ToolbarContent,
    ToolbarGroup,
    ToolbarItem,
    ToolbarToggleGroup,
} from "@patternfly/react-core";
import OutlinedPlayCircleIcon from "@patternfly/react-icons/dist/esm/icons/outlined-play-circle-icon";
import ExpandIcon from "@patternfly/react-icons/dist/esm/icons/expand-icon";
import PauseIcon from "@patternfly/react-icons/dist/esm/icons/pause-icon";
import PlayIcon from "@patternfly/react-icons/dist/esm/icons/play-icon";
import EllipsisVIcon from "@patternfly/react-icons/dist/esm/icons/ellipsis-v-icon";
import DownloadIcon from "@patternfly/react-icons/dist/esm/icons/download-icon";

import React from "react";
import {Client} from "@stomp/stompjs";
import {useEffect, useState, useRef} from "react";
import {log} from "./globals";
import {jolokiaService, workspace, MBeanNode} from '@hawtio/react'
import {IJolokiaSimple} from "@jolokia.js/simple";
import {ReadResponseValue} from "jolokia.js"

const SOCKET_URL = "ws://localhost:8080/hawtio-spring-boot-log-viewer";

export const LogViewer: React.FunctionComponent = () => {
    const [isPaused, setIsPaused] = useState<boolean>(false);
    const [isFullScreen, setIsFullScreen] = useState<boolean>(false);
    const [currentItemCount, setCurrentItemCount] = useState<number>(0);
    const [renderData, setRenderData] = useState<string>("");
    const [buffer, setBuffer] = useState<string[]>([]);
    const [linesBehind, setLinesBehind] = useState<number>(0);
    const logViewerRef = useRef<any>();
    const logViewerRefOuter = useRef<any>();

    useEffect(() => {
        if (!isPaused && buffer.length > 0) {
            setCurrentItemCount(buffer.length);
            setRenderData(buffer.join("\n"));
        } else if (buffer.length !== currentItemCount) {
            setLinesBehind(buffer.length - currentItemCount);
        } else {
            setLinesBehind(0);
        }
    }, [isPaused, buffer]);

    useEffect(() => {
        log.log("use effect executed");
        let onConnected = () => {
            log.log("connected");
            client.subscribe("/topic/new_logs", function (msg) {
                if (msg.body) {
                    const jsonBody = JSON.parse(msg.body);
                    if (jsonBody.message) {
                        setBuffer((prevLogs) => [...prevLogs, jsonBody.message]);
                        log.log("message received ", jsonBody.message);
                    }
                }
            });

            jolokiaService.getJolokia().then(function (jolokiaSimple: IJolokiaSimple) {
                return jolokiaSimple.getAttribute("io.github.erdinc09:category=springboot,name=FrontendParameters", "WebSocketUrl");
            }).then(function (responseValueAsString: ReadResponseValue) {
                console.log(`responseValueAsString : ${responseValueAsString}`)
            })

            // workspace.hasMBeans().then(function (result: boolean) {
            //   console.log(`hasMBeans : ${result}`)
            // });

            // workspace.findMBeans("io.github.erdinc09", {category:'springboot', name: "FrontendParameters" })
            // .then(function (mBeanNodes: MBeanNode[]) {
            //   console.log(`mBeanNodes : ${mBeanNodes}`)
            //   console.log(`mBeanNodes[0].getProperty("WebSocketUrl") = ${mBeanNodes[0].getProperty("WebSocketUrl")}`);
            // })
        };

        let onDisconnected = () => {
            log.log("disconnected");
        };

        const client = new Client({
            brokerURL: SOCKET_URL,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: onConnected,
            onDisconnect: onDisconnected,
        });

        client.activate();
    }, []);

    // Listening escape key on full screen mode.
    useEffect(() => {
        const handleFullscreenChange = () => {
            const isFullscreen = document.fullscreenElement;
            setIsFullScreen(!!isFullscreen);
        };

        document.addEventListener("fullscreenchange", handleFullscreenChange);
        return () => {
            document.removeEventListener("fullscreenchange", handleFullscreenChange);
        };
    }, []);

    const onExpandClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        const element: Element = document.querySelector(
            "#complex-toolbar-demo"
        ) as Element;

        //const element: Element = logViewerRefOuter.current;

        if (!isFullScreen) {
            if (element.requestFullscreen) {
                element.requestFullscreen();
            }
            setIsFullScreen(true);
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
            setIsFullScreen(false);
        }
    };

    const onDownloadClick = () => {
        const element = document.createElement("a");
        const file = new Blob([buffer.join("\n")], {
            type: "text/plain",
            endings: "native",
        });
        element.href = URL.createObjectURL(file);
        element.download = `log.txt`; //`${selectedDataSource}.txt`
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
    };

    const onScroll = ({
                          scrollDirection,
                          scrollOffset,
                          scrollOffsetToBottom,
                          //scrollUpdateWasRequested is false when the scroll event is cause by the user interaction in the browser, else it's true.
                          scrollUpdateWasRequested,
                      }: {
        scrollDirection: "forward" | "backward";
        scrollOffset: number;
        scrollOffsetToBottom: number;
        scrollUpdateWasRequested: boolean;
    }) => {
        log.log(
            `scrollDirection = ${scrollDirection}, scrollOffsetToBottom  = ${scrollOffsetToBottom},  scrollUpdateWasRequested = ${scrollUpdateWasRequested}`
        );
        if (!scrollUpdateWasRequested && scrollDirection == "backward") {
            if (scrollOffsetToBottom > 1) {
                setIsPaused(true);
            }
        }
        if (!scrollUpdateWasRequested && scrollDirection == "forward") {
            if (scrollOffsetToBottom < 1) {
                setIsPaused(false);
            }
        }
    };

    const ControlButton = () => (
        <Button
            variant="link"
            onClick={() => {
                setIsPaused(!isPaused);
            }}
            icon={isPaused ? <PlayIcon/> : <PauseIcon/>}
        >
            {isPaused ? ` Resume Log` : ` Pause Log`}
        </Button>
    );

    const leftAlignedToolbarGroup = (
        <React.Fragment>
            <ToolbarToggleGroup toggleIcon={<EllipsisVIcon/>} breakpoint="md">
                <ToolbarItem>
                    <LogViewerSearch
                        onFocus={(_e: any) => setIsPaused(true)}
                        placeholder="Search"
                        minSearchChars={4} // ?
                    />
                </ToolbarItem>
            </ToolbarToggleGroup>
            <ToolbarItem>
                <ControlButton/>
            </ToolbarItem>
        </React.Fragment>
    );

    const rightAlignedToolbarGroup = (
        <React.Fragment>
            <ToolbarGroup variant="icon-button-group">
                <ToolbarItem>
                    <Tooltip position="top" content={<div>Download</div>}>
                        <Button
                            onClick={onDownloadClick}
                            variant="plain"
                            aria-label="Download current logs"
                        >
                            <DownloadIcon/>
                        </Button>
                    </Tooltip>
                </ToolbarItem>
                <ToolbarItem>
                    <Tooltip position="top" content={<div>Expand</div>}>
                        <Button
                            onClick={onExpandClick}
                            variant="plain"
                            aria-label="View log viewer in full screen"
                        >
                            <ExpandIcon/>
                        </Button>
                    </Tooltip>
                </ToolbarItem>
            </ToolbarGroup>
        </React.Fragment>
    );

    const FooterButton = () => {
        const handleClick = (_e: React.MouseEvent<HTMLButtonElement>) => {
            setIsPaused(false);
        };
        return (
            <Button onClick={handleClick} isBlock>
                <OutlinedPlayCircleIcon/>
                resume {linesBehind === 0 ? null : `and show ${linesBehind} lines`}
            </Button>
        );
    };

    return (
        // <PageSection variant="default">
        <PatternFlyLogViewer
            data={renderData}
            theme="light"
            ref={logViewerRefOuter}
            // @ts-ignore id
            id="complex-toolbar-demo"
            scrollToRow={currentItemCount}
            innerRef={logViewerRef}
            height={"100%"}
            toolbar={
                <Toolbar>
                    <ToolbarContent>
                        <ToolbarGroup align={{default: "alignLeft"}}>
                            {leftAlignedToolbarGroup}
                        </ToolbarGroup>
                        <ToolbarGroup align={{default: "alignRight"}}>
                            {rightAlignedToolbarGroup}
                        </ToolbarGroup>
                    </ToolbarContent>
                </Toolbar>
            }
            overScanCount={10}
            footer={isPaused && <FooterButton/>}
            onScroll={onScroll}
        />
        //</PageSection>
    );
};
