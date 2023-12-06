
<h1 align="center">
  <br>
  <a href="https://rsps.dev/"><img src="https://cdn.discordapp.com/attachments/805977768226717717/1181814806886486026/image.png?ex=65826dcf&is=656ff8cf&hm=67690f4bc9e80a3512510f6473b8e20bca8e67e46c96b06bbc97ffb3083674ea&" alt="rsps.dev" width="200"></a>
  <br>
  rsps.dev
  <br>
</h1>

<h4 align="center">A Simple 317 and OSRS revision Cache Editor.</h4>

<p align="center">
  <a href="https://discord.gg/R792JfRetW"><img src="https://img.shields.io/badge/on_discord-rspsdev?logo=discord&logoColor=white&label=join%20us&color=34D399"></a>
</p>

<p align="center">
  <a href="#setup">Setup</a> •
  <a href="#download">Download</a> •
  <a href="#support">Support</a>
</p>

## Setup

```properties

git clone https://github.com/rsps-dev/rsps.dev.valkyr-cache-suite.git

open project in intelliJ

gradle buildAndMove

gradle ValkyrSuite run
```


## How To Use

1. First run the gradle buildAndMoveCommand and then run the gradle ValkyrSuite run command.

2. On first launch you'll need to set up your cache directory and region keys (XTEAS).

![screenshot](https://cdn.discordapp.com/attachments/805977768226717717/1181835551339454474/image.png?ex=65828120&is=65700c20&hm=71c3199b3d123c22ce78b484b4d9609d4ffe5fa6d68dfa8bf8122f97124dd723&)


> **Note:**
> You will need to restart the application after changing a cache directory.

3. After any cache modifications it is crucial that you click **Save** and then fully exit the application.
> **Note:**
> Sometimes Valkyr will hang, this means despite it being closed the application is still running in the background. You can kill the process in task manager or run this command in your terminal: `wmic process where "name like '%java%'" delete`


## Download

We will upload a pre-jarred Valkyr application soon...

## Support

We didn't develop Valkyr however we have made some additions, if you need any help you can always contact us in our [discord here](https://discord.gg/R792JfRetW).