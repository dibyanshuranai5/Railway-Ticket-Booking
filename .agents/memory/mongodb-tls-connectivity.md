---
name: MongoDB TLS connectivity
description: A backend MongoDB connection in this workspace failed during TLS negotiation.
---

The initial backend connection to MongoDB failed during TLS negotiation with a remote `internal_error` alert. Recheck the current environment and database network access before assuming an application-code fault. Do not disable certificate or hostname verification to bypass this.

**Why:** The API can start and serve health checks, but database-backed operations cannot complete while the remote TLS connection fails.

**How to apply:** If MongoDB operations still fail in this workspace, first verify the current host/network allowlist and TLS connectivity; then test the application against a reachable database without exposing the connection URI.
