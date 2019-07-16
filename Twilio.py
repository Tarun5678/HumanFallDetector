from twilio.rest import Client


def sendMessage(ecName , ecPhone):
    account_sid = 'AC178c0f11b40186ecffe73a3d4cb198d1'
    auth_token = '00f303bf5caaa424a3e2a0535e7ee3ad'
    client = Client(account_sid, auth_token)
    message = client.messages \
        .create(
        body="hi"+""+ecName+""+"Fall Detected",
        from_='+17098006894',
        to=ecPhone
    )

    print(message.sid)
