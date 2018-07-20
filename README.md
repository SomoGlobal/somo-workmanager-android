# somo-workmanager-android
AnalyticsWorkManager : Demo of WorkManager with Androd Architecture Components

A sample app to demonstrate how WorkManager works in executing different Workers, in sequence and parallel along with chaining.

GetConfigWorker - The app gets a config from the server(Mocked API) and checks for the required analytics needed
BatteryStatWorker and NetworkUsageWorker - The app starts to gather different analytics in parallel, based on the server config.

ReportToServerWorker - The merged result is then sent to the server.

HomeViewModel - Builds all the requests, chains them and schedules them.
