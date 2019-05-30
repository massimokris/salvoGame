var gamesJs;

function gamesList(){

    var list = '';

    for(var i = 0; i < gamesJs.games.length; i++){

        list += "<li> Game: "+gamesJs.games[i].gameId+" Date: "+gamesJs.games[i].creationDate+"<ol>";

        for(var j = 0; j <gamesJs.games[i].gamePlayers.length; j++){

            list += "<li>"+gamesJs.games[i].gamePlayers[j].player.email+"</li>";
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

fetch( "/api/games", {
}).then(function(games) {

    if (games.ok) {

        return games.json();
    }

    throw new Error(games.statusText);
}).then(function(value) {

    gamesJs = value;
    document.getElementById("gameList").innerHTML = gamesList();
    document.getElementById("gameTable").innerHTML = statisticsTable();
    totalScore();
    totalWon();
    totalLost();
    totalTied();

}).catch(function(error) {

    console.log( "Request failed: "+ error.message );
});