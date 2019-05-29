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

function totalScore(x){

    var list = x;
    var total = 0;

    for(var i = 0; i < list.length; i++){

        total += list[i];
    }

    return total;
}

function totalWon(x){

    var list = x;
    var total = 0;

    for(var i = 0; i < list.length; i++){

        if(list[i] == 3){

            total ++;
        }
    }

    return total;
}

function totalLost(x){

    var list = x;
    var total = 0;

    for(var i = 0; i < list.length; i++){

        if(list[i] == 0){

            total ++;
        }
    }

    return total;
}

function totalTied(x){

    var list = x;
    var total = 0;

    for(var i = 0; i < list.length; i++){

        if(list[i] == 1){

            total ++;
        }
    }

    return total;
}

function statisticsTable(){

    var table = '<tr> <th>Name</th> <th>Total</th> <th>Won</th> <th>Lost</th> <th>Tied</th></tr>';
    var list = [];
    var score, won, lost, tied;

    for(var i = 0; i < gamesJs.length; i++){

            for(var j = 0; j < gamesJs[i].gamePlayers.length; j++){

                if(!(list.includes(gamesJs[i].gamePlayers[j].player.email))){

                    score = totalScore(gamesJs[i].scores);
                    won = totalWon(gamesJs[i].scores);
                    tied = totalTied(gamesJs[i].scores);
                    lost = totalLost(gamesJs[i].scores);
                    table += '<tr><td>'+gamesJs[i].gamePlayers[j].player.email+
                    '</td><td>'+score+'</td><td>'+won+'</td><td>'+lost+'</td><td>'+tied+'</td></tr>';
                    list += gamesJs[i].gamePlayers[j].player.email;
                }
            }
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
    playerList();
    document.getElementById("gameTable").innerHTML = statisticsTable();
    totalScore();
    totalWon();
    totalLost();
    totalTied();

}).catch(function(error) {

    console.log( "Request failed: "+ error.message );
});