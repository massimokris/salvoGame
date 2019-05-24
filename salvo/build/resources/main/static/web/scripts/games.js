var gamesJs;

function gamesList(){

    var list = '';

    for(var i = 0; i < gamesJs.length; i++){

        list += "<li> Game: "+gamesJs[i].gameId+" Date: "+gamesJs[i].creationDate+"<ol>";

        for(var j = 0; j <gamesJs[i].gamePlayers.length; j++){

            list += "<li>"+gamesJs[i].gamePlayers[j].player.email+"</li>";
        }

        list += "</ol></li>";
    }

    return list;
}

fetch( "/api/games", {
}).then(function(games) {

    if (games.ok) {

        return games.json();
    }

    throw new Error(games.statusText);
}).then(function(value) {

    gamesJs = value;
    document.getElementById("gameList").innerHTML = gamesList();

}).catch(function(error) {

    console.log( "Request failed: "+ error.message );
});