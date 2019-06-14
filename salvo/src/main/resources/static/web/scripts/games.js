var gamesJs;
var currentUser;
var username = document.getElementById("username");
var password = document.getElementById("password");
var gameTojoin;

function gamesList(){

    var list = '';

    for(var i = 0; i < gamesJs.games.length; i++){

        list += "<li> Game: "+gamesJs.games[i].gameId+" Date: "+gamesJs.games[i].creationDate+"<ol>";

        for(var j = 0; j <gamesJs.games[i].gamePlayers.length; j++){

            if(gamesJs.games[i].gamePlayers[j].player.email == currentUser){

                list += "<li>"+gamesJs.games[i].gamePlayers[j].player.email+"<a href='/web/game.html?Gp="+gamesJs.games[i].gamePlayers[j].gamePlayerId+"'> Your game</a>"+"</li>";
            }else if(gamesJs.games[i].gamePlayers.length < 2 &&
                     gamesJs.games[i].gamePlayers[j].player.email != currentUser &&
                     currentUser != null){

                gameTojoin = gamesJs.games[i];
                list += "<li>"+gamesJs.games[i].gamePlayers[j].player.email+"<a href='/web/game.html?Gp="+gamesJs.games[i].gamePlayers[j].gamePlayerId+"'> Join game</a>"+"</li>";
            }else{

                list += "<li>"+gamesJs.games[i].gamePlayers[j].player.email+"</li>";
            }
        }

        list += "</ol></li>";
    }

    return list;
}

function statisticsTable(){

    var table = '<tr> <th>Name</th> <th>Total</th> <th>Won</th> <th>Lost</th> <th>Tied</th></tr>';
    var list = [];
    var score, won, lost, tied;

    for(var i = 0; i < gamesJs.leaderboard.length; i++){

            //for(var j = 0; j < gamesJs[i].gamePlayers.length; j++){

                if(!(list.includes(gamesJs.leaderboard[i].email))){

                    score = gamesJs.leaderboard[i].score;
                    won = gamesJs.leaderboard[i].won;
                    tied = gamesJs.leaderboard[i].tied;
                    lost = gamesJs.leaderboard[i].lost;

                    table += '<tr><td>'+gamesJs.leaderboard[i].email+
                    '</td><td>'+score+'</td><td>'+won+'</td><td>'+lost+'</td><td>'+tied+'</td></tr>';
                    list += gamesJs.leaderboard[i].email;
                }
            //}
        }

    return table;
}

function fetchGames(){

    fetch( "/api/games", {
    }).then(function(games) {

        if (games.ok) {

            return games.json();
        }

        throw new Error(games.statusText);
    }).then(function(value) {

        gamesJs = value;
        if(gamesJs.player != null){

            currentUser = gamesJs.player.email;
        }else{

            currentUser = gamesJs.player;
        }
        document.getElementById("gameList").innerHTML = gamesList();
        document.getElementById("gameTable").innerHTML = statisticsTable();
        document.getElementById("current").innerHTML = currentName();
        showForm();

    }).catch(function(error) {

        console.log( "Request failed: "+ error.message );
    });
}

fetchGames();

function currentName(){

    if(currentUser == null){

        document.getElementById('current').style.display="none";
    }else{

        document.getElementById('current').style.display="block";
    }

    return currentUser;
}

function showForm(){

    if(currentUser == null){

        document.getElementById('login-form').style.display="block";
        document.getElementById('logout-form').style.display="none";
        document.getElementById('createGame').style.display="none";
    }else{

        document.getElementById('login-form').style.display="none";
        document.getElementById('logout-form').style.display="block";
        document.getElementById('createGame').style.display="block";
    }
}

showForm();

function login() {

  $.post("/api/login",
         { username: username.value,
           password: password.value })
  .done(function(){

        fetchGames();
        //showForm();
        //location.reload();
  })
  .fail();
}

function signup() {

  $.post("/api/players",
         { username: username.value,
           password: password.value })
   .done(function(){

        login();
        fetchGames();
        //showForm();
        //location.reload();
   })
   .fail();
}

function logout() {

  $.post("/api/logout")
   .done(function(){

        username.value = "";
        password.value = "";
        fetchGames();
        //showForm();
        //location.reload();
   })
   .fail();
}

function joinGame() {

  $.post("/api/games",
         { currentUser,
           gameTojoin,
           })
   .done(function(){

        login();
        fetchGames();
        //showForm();
        //location.reload();
   })
   .fail();
}

function createGame(){

    $.post("/api/games")
    .done(function(data){
        return = "/web/game.html?gp="+data.gamePlayerId;
        fetchGames();
    })
    .fail();
}
