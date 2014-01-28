set MICA_DEMO_OPTS="-noasyncgc -noclassgc"
call "MiRunApp.bat" MiPerformanceTestSuite -file main.perf
set MICA_DEMO_OPTS=
