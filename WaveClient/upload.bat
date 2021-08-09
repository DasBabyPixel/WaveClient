@echo off
echo File to upload: %*
java -jar uploader.jar %*
pause > nul