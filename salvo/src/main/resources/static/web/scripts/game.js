var gamePlayer;

function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}

var game = paramObj(window.location.href);

function setLocations(){

    for(var i = 0; i < gamePlayer.Ships.length; i++){

        var x = +(gamePlayer.Ships[i].locations[0].slice(1,3)) - 1;
        var y = gamePlayer.Ships[i].locations[0].slice(0,1).toUpperCase().charCodeAt(0) - 65;
        var type = gamePlayer.Ships[i].type.toLowerCase();
        var w, h, orientation;

        if(gamePlayer.Ships[i].locations[0][0] == gamePlayer.Ships[i].locations[1][0]){

            w = gamePlayer.Ships[i].locations.length;
            h = 1;
            orientation = "Horizontal";
        }else{

            h = gamePlayer.Ships[i].locations.length;
            w = 1;
            orientation = "Vertical";
        }

        grid.addWidget($('<div id="'+type+'"><div class="grid-stack-item-content '+type+orientation+'"></div><div/>'),
                x, y, w, h);

    }
}

function setNames(){

    var names = '';
    if(gamePlayer.gamePlayers.length > 2){
        if(game == gamePlayer.gamePlayers[0].gamePlayerId){

            names += gamePlayer.gamePlayers[0].player.email +" (you)  vs.  "+ gamePlayer.gamePlayers[1].player.email;
        }else{
            names += gamePlayer.gamePlayers[1].player.email +" (you)  vs.  "+ gamePlayer.gamePlayers[0].player.email;
        }
    }else{

        names += " (you)  vs. ";
    }

    return names;
}



fetch( "/api/game_view/"+game.Gp, {
}).then(function(games) {

    if (games.ok) {

        return games.json();
    }

    throw new Error(games.statusText);
}).then(function(value) {

    gamePlayer = value;
    setNames();
    loadGrid();
    createGrid(11, $(".grid-salvos"),"sa")
    document.getElementById("names").innerHTML = setNames();
    setSalvos();
    paint(x, y, g, t);

}).catch(function(error) {

    console.log( "Request failed: "+ error.message );
});

function setSalvos(){

    var you;
    var vs;
    var x;
    var y;
    var g;
    var turn;

    if(gamePlayer.gameId == gamePlayer.gamePlayers[0].gamePlayerId){

        you = gamePlayer.Salvos[0];
        vs = gamePlayer.Salvos[1];
    }else{
        you = gamePlayer.Salvos[1];
        vs = gamePlayer.Salvos[0];
    }

    for(var i = 0; i < you.locations.length; i++){

        x = +(you.locations[i].slice(1,3)) - 1;
        y = you.locations[i].slice(0,1).toUpperCase().charCodeAt(0) - 65;
        g = "sh";
        turn = you.turn;
        paint(x, y, g, turn);
    }

    for(var i = 0; i < you.locations.length; i++){

        x = +(vs.locations[i].slice(1,3)) - 1;
        y = vs.locations[i].slice(0,1).toUpperCase().charCodeAt(0) - 65;
        g = "sa";
        turn = vs.turn;
        paint(x, y, g, turn);
    }
}

function paint(x, y, g, t){

    var location =  g + y + x;

    document.getElementById(location).innerHTML = "<p align='center'>"+t+"</p>";
    document.getElementById(location).classList.add("paint");

    if($(location).hasClass('busy-cell')){

    }
}

//main function that shoots the gridstack.js framework and load the grid with the ships
const loadGrid = function () {
    var options = {
        //10 x 10 grid
        width: 10,
        height: 10,
        //space between elements (widgets)
        verticalMargin: 0,
        //height of cells
        cellHeight: 45,
        //disables resizing of widgets
        disableResize: true,
        //floating widgets
		float: true,
        //removeTimeout: 100,
        //allows the widget to occupy more than one column
        disableOneColumnMode: true,
        //false allows widget dragging, true denies it
        staticGrid: false,
        //activates animations
        animate: true
    }

    //grid initialization
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');

    setLocations();

    createGrid(11, $(".grid-ships"),"sh")

    rotateShips("carrier", 5)
    rotateShips("battleship", 4)
    rotateShips("submarine",3)
    rotateShips("destroyer", 3)
    rotateShips("patrol_boat",2)

    listenBusyCells()
    $('.grid-stack').on('change', listenBusyCells)


    //all the functionalities are explained in the gridstack github
    //https://github.com/gridstack/gridstack.js/tree/develop/doc

}


//creates the grid structure
const createGrid = function(size, element, id){

    let wrapper = document.createElement('DIV')
    wrapper.classList.add('grid-wrapper')

    for(let i = 0; i < size; i++){
        let row = document.createElement('DIV')
        row.classList.add('grid-row')
        row.id =id+`grid-row${i}`
        wrapper.appendChild(row)

        for(let j = 0; j < size; j++){
            let cell = document.createElement('DIV')
            cell.classList.add('grid-cell')
            if(i > 0 && j > 0)
            cell.id = id+`${i - 1}${ j - 1}`

            if(j===0 && i > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = String.fromCharCode(i+64)
                cell.appendChild(textNode)
            }
            if(i === 0 && j > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = j
                cell.appendChild(textNode)
            }
            row.appendChild(cell)
        }
    }

    element.append(wrapper)
}

//adds a listener to the ships, wich shoots its rotation when clicked
const rotateShips = function(shipType, cells){

        $(`#${shipType}`).click(function(){
            let x = +($(this).attr('data-gs-x'))
            let y = +($(this).attr('data-gs-y'))
        if($(this).children().hasClass(`${shipType}Horizontal`)){
            if(y + cells - 1 < 10){
                grid.resize($(this),1,cells);
                $(this).children().removeClass(`${shipType}Horizontal`);
                $(this).children().addClass(`${shipType}Vertical`);
            } else{
                grid.update($(this), null, 10 - cells)
                grid.resize($(this),1,cells);
                $(this).children().removeClass(`${shipType}Horizontal`);
                $(this).children().addClass(`${shipType}Vertical`);

            }

        }else{
            if(x + cells - 1  < 10){
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            } else{
                grid.update($(this), 10 - cells)
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            }

        }
    });

}

//loops over all the grid cells, verifying if they are empty or busy
const listenBusyCells = function(){
    for(let i = 0; i < 10; i++){
        for(let j = 0; j < 10; j++){
            if(!grid.isAreaEmpty(i,j)){
                $(`#${j}${i}`).addClass('busy-cell').removeClass('empty-cell')
            } else{
                $(`#${j}${i}`).removeClass('busy-cell').addClass('empty-cell')
            }
        }
    }
}
