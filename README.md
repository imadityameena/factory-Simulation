# Factory Machine Simulation System

This project is a Java Swing desktop application for simulating factory machine failures, repair queues, and adjuster utilization.

## Features

- Machine category management
- Adjuster management with expertise lists
- Event-driven simulation of failures and repairs
- Machine and adjuster utilization metrics
- Queue and waiting time analysis
- Summary tables and recommendations

## Run

One command (Windows cmd or PowerShell):

```cmd
run.cmd
```

This compiles the project and launches the Swing app.

If you have a JDK installed, compile and run with:

```powershell
if (!(Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
$sources = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $sources
java -cp out com.factorysimulation.App
```

You can also open the project in an IDE such as VS Code or IntelliJ and run `com.factorysimulation.App`.

## Notes

- The UI is implemented with Java Swing only.
- Sample machine and adjuster data is loaded on startup so you can run the simulation immediately.
