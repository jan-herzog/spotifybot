<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <title>Dashboard | Spitchbot</title>
    <meta name="description" content="Twitch Chatbot integrating Spotify API">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootswatch@5.1.3/dist/darkly/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Lato:ital,wght@0,400;0,700;1,400&amp;display=swap">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Arbutus+Slab">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
    <link rel="stylesheet" href="assets/css/styles.min.css">
</head>
<body style="background-image: linear-gradient(to bottom right, #eb01a5, #d13531);background-attachment: fixed;margin: 0;padding: 0;">
<nav class="navbar navbar-dark navbar-expand-md" style="z-index: 2;position: absolute;width: 100%;padding: 20px;">
    <div class="container-fluid"><a class="navbar-brand" href="#">Spitchbot</a>
        <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-1"><span
                    class="visually-hidden">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="navcol-1">
            <ul class="navbar-nav">
                <li class="nav-item"><a class="nav-link" href="/" data-bs-target="/">Home</a></li>
                <li class="nav-item"><a class="nav-link active" href="/dashboard">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="/team">Team</a></li>
            </ul>
            <a class="btn ms-auto" role="button" href="/logout">Log Out</a></div>
    </div>
</nav>
<div class="container" style="padding-top: 200px;">
    <div class="row justify-content-md-center">
        <div class="col-8"
             style="padding: 50px;background-image: linear-gradient(to bottom right, #2d2d2d, #151515);border-radius: 20px;">
            <div class="row justify-content-md-center">
                <div class="col">
                    <div><span class="h1">Hello,&nbsp;</span><span class="h3">${username}</span></div>
                    <div style="font-size: 18px;">
                        <span style="padding: 20px 5px 20px 20px;">Spotify connected:&nbsp;</span>
                        <#if spotifyConnected>
                            <i class="fa fa-check" style="color: lime;"></i>
                        <#else>
                            <i class="fa fa-remove" style="color: red;"></i>
                        </#if>
                    </div>
                </div>
                <div class="col text-end align-self-center">
                    <div>
                        <#if spotifyConnected>
                            <a class="btn btn-danger btn-l" role="button" href="/spotify/unlink">Disconnect Spotify</a>
                        <#else>
                            <a class="btn btn-dark btn-lg" role="button"
                               href="/spotify/login">Connect
                                Spotify</a>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row justify-content-md-center" style="margin-top: 50px;">
        <div class="col-8"
             style="padding: 50px;background-image: linear-gradient(to bottom right, #2d2d2d, #151515);border-radius: 20px;">
            <div class="row justify-content-md-center">
                <div class="col">
                    <div>
                        <h2>Chat Account</h2>
                        <p>If you want to use your own chat account instead of the default 'Spitchbot' account, you
                            can easily connect it here.</p>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <#if botUsername == "null">
                        <a class="btn btn-dark btn-lg" role="button" href="/account/link">Connect</a>
                    <#else>
                        <a class="btn btn-danger btn-lg" role="button" href="/account/unlink">Disconnect</a>
                        <span style="padding-left: 20px;">Connected account:</span>
                        <span class="fw-bold text-info" style="padding-left: 5px;">${botUsername}</span>
                    </#if>
                </div>
            </div>
        </div>
    </div>
    <div class="row justify-content-md-center" style="margin-top: 50px;">
        <div class="col-8"
             style="padding: 50px;background-image: linear-gradient(to bottom right, #2d2d2d, #151515);border-radius: 20px;">
            <div class="row justify-content-md-center">
                <div class="col">
                    <div>
                        <h2>Commands</h2>
                        <p>There are quite a few commands spitch is providing. Here is a short (and temporary) list of all commands.</p>
                        <ul>
                            <li>!song - Show information about the current playing song</li>
                            <li>!lastsong - Show information about the last played song</li>
                            <li>!svolume (+/-amount) - Shows/changes the current spotify playback volume</li>
                            <li>!spause - Pauses playback</li>
                            <li>!splay - Starts playback</li>
                            <li>!splayadd (name of the song) - Adds song to playback</li>
                            <li>!sskip - Skips current song</li>
                            <li>!sprevious - Goes back to the last song</li>
                        </ul>

                        <h2>Songrequest</h2>
                        <p>You do get a custom chanel point reward created. You can change its properties but not the title. It works like !splayadd</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<iframe style="display:none" name="hidden"></iframe>
<footer
        style="text-align: center;position: relative;z-index: 2;bottom: 0;left: 50%;transform: translate(-50%);margin-top: 10px;">
        <span class="text-white" style="color: #cccccc;padding: 10px;">Made with&nbsp;<i class="fa fa-heart"
                                                                                         style="color: red;transform: scale(0.70);"></i>&nbsp;by&nbsp;<a
                    href="https://twitch.tv/kexesser"
                    style="text-decoration: none;color: inherit;" target="_blank">Kexesser</a></span></footer>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="assets/js/script.min.js"></script>
</body>
</html>
