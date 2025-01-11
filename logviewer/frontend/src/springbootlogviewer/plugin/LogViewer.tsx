import { PageSection, Text, TextContent } from "@patternfly/react-core";
import React from "react";
import { Client } from "@stomp/stompjs";
import { useEffect, useState } from "react";
import { TextArea } from "@patternfly/react-core";
import { log } from "./globals";
const SOCKET_URL = "ws://localhost:8080/hawtio-spring-boot-log-viewer";

type Log = {
  message: string;
};

export const LogViewer: React.FunctionComponent = () => {
  const [logs, setLogs] = useState<Log[]>([]);

  useEffect(() => {
    log.log("use effect executed");
    let onConnected = () => {
      log.log("connected");
      client.subscribe("/topic/new_logs", function (msg) {
        if (msg.body) {
          var jsonBody = JSON.parse(msg.body);
          if (jsonBody.message) {
            setLogs((prevLogs) => [{ message: jsonBody.message }, ...prevLogs]);
            log.log("message received ", jsonBody.message);
          }
        }
      });
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

  return (
    <PageSection variant="light">
      <TextArea
        aria-label="invalid text area example"
        resizeOrientation="both"
        value={logs
          .map((val) => val.message)
          .reduce((acc, val) => val + "\n" + acc, "")}
      />
    </PageSection>
  );
};
