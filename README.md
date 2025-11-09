# GT Trading Poker Discretionary Team

## How To Run
## Install
- Make sure you use maven to install the dependencies
- If you do not have mlflows installed install it via
```
pip install mlflow
```
## Running
- Start MLFlow server
```
mlflow server --backend-store-uri sqlite:///mlflow.db --default-artifact-root ./mlruns --host 127.0.0.1 --port 5000
```
- When running the script provide "http://127.0.0.1:5000" as the logging url
- To view runs visit http://127.0.0.1:5000 in your browser

