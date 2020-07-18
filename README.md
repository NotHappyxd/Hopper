# Hopper
Hopper is a versatile license system for java programs. It can easily be programmed to meat your needs!

# Getting Started
---
# Fork the `NotHappyXD/Hopper` repository
You will need to compile the server jar and get the source to change things for your needs.

Once you found a place for this repository, you can change it how you like.

You will need Maven to compile this repository.
When you have it installed, execute this command.
```
mvn clean package
```

Now go into the Server/target folder and drag the jar to your server.
Then execute this command

```
java -Xms1G -Xmx1G -jar Server.jar
```

Voalla, your license system is running.

Now, put the Client class into your project and change the method name if needed.

Now, you have to change the address to your server.
Locate the line that contains ``new Socket("localhost", 3926")`` and change it to your server's address.

Then, it is ready.
---

# Common Issues
---

# If you or the person using the license is having an issue, it could be one of these.

- Your server is blocking incoming traffic. To fix this, your server needs to whitelist the outgoing port. By default this port is 3926.
- The machine is blocking outgoing connections. To fix this, the person using the license has to whitelist the license server's ip.
---

# Keep in mind that this is not production-ready and may contain bugs. This project will be worked on in my free time. Please make a issue on github if you spot any issues and make a pull request if you have any changes.
