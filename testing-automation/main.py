import os
import pprint
import time

from pexpect import pxssh
from collections import OrderedDict
from dotenv import load_dotenv
import requests

load_dotenv()
REMOTE_HOSTNAME = os.getenv("REMOTE_HOSTNAME")
REMOTE_USERNAME = os.getenv("REMOTE_USERNAME")
SSH_KEY = os.getenv("SSH_KEY")
DMT_LOCATION = os.getenv("DMT_LOCATION")
COUNT = [100, 200, 500, 1000]
ROWS = [20, 100, 2000]
REPETITIONS = 5


def execute_tests(counts, max_rows):
    total_results = OrderedDict()
    for i in counts:
        count_result = OrderedDict()
        for j in max_rows:
            print("Starting tests for count: {}, rows: {}".format(i, j))
            result = multiple_generate_requests(i, j, REPETITIONS)
            count_result["Rows: {}".format(j)] = result
            send_reset_database_request()
            print("Finished tests for count: {}, rows: {}".format(i, j))
        total_results["Count: {}".format(i)] = count_result
    return total_results


def get_ssh_client():
    s = pxssh.pxssh()
    s.login(server=REMOTE_HOSTNAME, username=REMOTE_USERNAME, ssh_key=SSH_KEY)
    s.prompt()
    print("Logged in successfully")
    return s


def send_generate_request(count, max_rows):
    print("Sending generate request")

    params = {
        'count': count,
        'maxRows': max_rows,
    }
    url = "http://{}:8080/Main/GenerateBatchLoad"
    for i in range(5):
        try:
            response = requests.post(url.format(REMOTE_HOSTNAME), params=params)
        except requests.exceptions.ConnectionError:
            if i != 5:
                print("Connection error when sending generate request, will retry")
                time.sleep(5)
                continue
            raise
    # print("Results for count: {} and max rows: {}".format(count, max_rows))
    # print(response.json()["last executor started (ms)"])
    # print(response.json()["last executor finished (ms)"])
    # print(response.json()["total time (ms)"])
    return rename_response(response.json())


def multiple_generate_requests(count, max_rows, repeat):
    best_response = {}
    for i in range(repeat):
        response = send_generate_request(count, max_rows)
        if len(best_response) == 0\
                or best_response["last executor finished (ms)"] > response["last executor finished (ms)"]:
            best_response = response
    return best_response


def send_reset_database_request():
    print("Sending reset database request")
    url = "http://{}:8080/Main/ResetDatabase"
    for i in range(5):
        try:
            response = requests.get(url.format(REMOTE_HOSTNAME))
        except requests.exceptions.ConnectionError:
            if i != 5:
                print("Connection error when sending reset database request, will retry")
                time.sleep(5)
                continue
            raise
    if response.status_code == 200:
        print("Database restarted successfully")
    else:
        print("Error when restarting database")
        raise Exception("Error when restarting database")


def start_dmt(ssh_client):
    start_command = "java -jar {}/target/quarkus-app/quarkus-run.jar &"
    ssh_client.sendline(start_command.format(DMT_LOCATION))
    ssh_client.prompt()
    ssh_client.sendline("echo $!")
    ssh_client.prompt()
    __pid = ssh_client.before.decode('UTF-8').splitlines()[1]
    print("Starting dmt with pid " + __pid)
    time.sleep(5)
    return __pid


def stop_dmt(ssh_client, process):
    kill_command = "kill -INT {}"
    ssh_client.sendline(kill_command.format(process))
    ssh_client.prompt()
    print("stopped dmt")


def rename_response(response):
    response["lef"] = response.pop("last executor finished (ms)")
    response["les"] = response.pop("last executor started (ms)")
    response["tt"] = response.pop("total time (ms)")
    return response


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    client = get_ssh_client()
    pid = start_dmt(client)
    results = execute_tests(COUNT, ROWS)
    pprint.pprint(results)
    stop_dmt(client, pid)







