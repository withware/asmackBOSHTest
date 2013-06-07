asmackBOSHTest
==============

Purpose:

A test app using Asmack that does simple chat over XMPP with Google Talk, and over BOSH with viprod server, to demonstrate our issue with BOSH.

Some helpful instructions:

The app launches with a settings screen that will remember whatever you have in it when you click ok. It remembers XMPP and BOSH modes seperately, and has default values for everything except your Google account.

XMPP Accounts:

It's Google Talk, use Google accounts.

BOSH Accounts:

test1@viprod.com, password test1

test2viprod.com, password test2

The issue:

With BOSH you connect to the server with two connections, one connection is always waiting for a response from the server and the server will send that response after x time or when a message needs to be sent to the app. The other connection is available for the app to instantly send a message to the server.
However using Asmack, the app acts as if only one connection is open, outgoing messages are not sent until a message is first received. So if you send a message the recipient will not see that message for up to a minute but if eg 20 seconds later the recipient sends a message to you, he will then instantly see the message you sent earlier because his message triggered the sending of your first message.
The app also chokes when you send multiple messages, because the first message is still waiting for a server response before it is sent. Only when the first message is sent is the second message queued and waiting for another server response before finally being sent.
Using Gajim as a 'control group' we can see it does BOSH properly, and how the app does not. The app instantly receives any messages sent from Gajim, but Gajim only receives messages from the app after a delay or response as explained above.
Using regular XMPP as another control method, the problem is gone.
