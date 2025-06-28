package uk.akane.aether

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object WebhookServer {

    fun startWebhookServer(operation: (String) -> Unit) {
        println("Starting webhook server...")
        embeddedServer(Netty, port = 9080) {
            routing {
                post("/github") {
                    val payload = call.receiveText()
                    val eventType = call.request.headers["X-GitHub-Event"] ?: "unknown"

                    when (eventType) {
                        "push" -> handlePushEvent(payload, operation)
                        "workflow_run" -> handleWorkflowEvent(payload, operation)
                        else -> println("Unknown event: $eventType")
                    }

                    call.respondText("OK")
                }
                get("/ping") {
                    call.respondText("pong")
                }
            }
        }.start(wait = false)
    }

    fun handlePushEvent(json: String, operation: (String) -> Unit) {
        val payload = Json.parseToJsonElement(json).jsonObject
        val repo = payload["repository"]?.jsonObject?.get("full_name")?.jsonPrimitive?.content
        val pusher = payload["pusher"]?.jsonObject?.get("name")?.jsonPrimitive?.content
        val commits = payload["commits"]?.jsonArray?.joinToString("\n") {
            val commit = it.jsonObject
            val message = commit["message"]?.jsonPrimitive?.content ?: ""
            val url = commit["url"]?.jsonPrimitive?.content ?: ""
            "- $message\n  $url"
        }

        operation.invoke(
            "$pusher 提交了新的 Commit 到 $repo\n\n$commits"
        )
    }

    fun handleWorkflowEvent(json: String, operation: (String) -> Unit) {
        val payload = Json.parseToJsonElement(json).jsonObject
        val workflow = payload["workflow_run"]?.jsonObject ?: return
        val name = workflow["name"]?.jsonPrimitive?.content
        val status = workflow["conclusion"]?.jsonPrimitive?.content
        val repo = payload["repository"]?.jsonObject?.get("full_name")?.jsonPrimitive?.content
        val action = payload["action"]?.jsonPrimitive?.content ?: ""

        if (action != "completed") return
        operation.invoke(
            "CI 构建完成:\n$repo\n\n工作流: $name\n结果: $status"
        )
    }

}