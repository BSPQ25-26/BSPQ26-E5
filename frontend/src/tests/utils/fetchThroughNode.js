import http from "node:http";
import https from "node:https";
import { URL } from "node:url";

export const fetchThroughNode = (input, init = {}) =>
    new Promise((resolve, reject) => {
        const requestUrl = new URL(input);
        const client = requestUrl.protocol === "https:" ? https : http;

        const request = client.request(
            requestUrl,
            {
                method: init.method || "GET",
                headers: init.headers,
            },
            (response) => {
                const chunks = [];

                response.on("data", (chunk) => chunks.push(chunk));
                response.on("end", () => {
                    const body = Buffer.concat(chunks).toString("utf8");

                    resolve({
                        ok: response.statusCode >= 200 && response.statusCode < 300,
                        status: response.statusCode,
                        text: async () => body,
                        json: async () => JSON.parse(body),
                    });
                });
            }
        );

        request.on("error", reject);

        if (init.body) {
            request.write(init.body);
        }

        request.end();
    });
