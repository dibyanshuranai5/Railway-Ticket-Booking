---
name: Live railway data policy
description: The user chose to wait for a real railway data provider rather than display mock train data.
---

Do not fabricate train schedules, availability, fares, or running status. Train search and runtime status should remain explicitly unavailable until a legitimate railway provider is chosen and connected.

**Why:** The user selected “wait for a live API provider” for train-data behavior.

**How to apply:** Keep the provider adapter replaceable and show a clear unavailable state until a real provider returns data.
