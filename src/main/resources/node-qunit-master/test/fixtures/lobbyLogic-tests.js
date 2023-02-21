const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

// var cytoscape; // wersja 3.18.2 jak u klienta
/*
    lepszą opcją jest mokowanie cytoscope jakimś innym obiektem bo ten moduł i trzeba zamieniacsporo rzeczy w kodzie
*/

/*Uwaga!
    jeśli zmienie nazwe template.html to nie wiem co się stanie bez reject_ w JSDOM dom

*/
var lobbyCodeMock = "BkaeDoPJ";
var playerMock = () => [
    {
        url: "/api/v1/lobby/join/"+lobbyCodeMock,
        responseText: { },
        type: "POST"
    },
    {
        url: '/api/v1/playerinfo',
        responseText: { 
            "username": "mockUsername",
            "nickname": "mockNickname",
            "isHost": false,
            "gameStarted": false,
            "gameCode": "mockGameCode"
        },
        type: "GET"
    },
    {
        url: '/api/v1/account/info',
        responseText: { 
            "authenticated": true,
            "email": "mockemail@cos.com",
            "emailVerified": false,
            "username": "mockUsername",
            "nickname": "mockNickname",
            "roles": ["Player"]
        },
        type: "GET"
    },
    {
        url: "/api/v1/lobby/"+lobbyCodeMock,
        responseText: { 
            "exists": true,
            "allowsRandomPlayers": true,
            "isFull": false,
            "maxPlayers": 32,
            "host": {
                    "username": "mockHostName",
                    "nickname": "mockHostName",
                    "roles": ["Player"]
                },
            "players": [{
                        "username": "mockPlayerName",
                        "nickname": "mockPlayerName"
            }]
        },
        type: "GET"
    },
    {
        url: "/api/v1/lobby/"+lobbyCodeMock+"/changes",
        responseText: { 
            "lobbyExists": true,
            "lobbyContentChanged": false,
        },
        type: "GET"
    }
];

var hostMock = () =>[
    {
        url: '/api/v1/playerinfo',
        responseText: { 
            "username": "mockUsername",
            "nickname": "mockNickname",
            "isHost": true,
            "gameStarted": false,
            "gameCode": "mockGameCode"
        },
        type: "GET"
    },
    {
        url: '/api/v1/account/info',
        responseText: { 
            "authenticated": true,
            "email": "mockemail@cos.com",
            "emailVerified": false,
            "username": "mockUsername",
            "nickname": "mockNickname",
            "roles": ["Player"]
        },
        type: "GET"
    },
    {
        url: "/api/v1/lobby/" + lobbyCodeMock,
        responseText: { 
            "exists": true,
            "allowsRandomPlayers": true,
            "isFull": false,
            "maxPlayers": 32,
            "host": {
                    "username": "mockUsername",
                    "nickname": "mockNickname",
                    "roles": ["Player"]
                },
            "players": [{
                        "username": "mockPlayerName",
                        "nickname": "mockPlayerName"
            }]
        },
        type: "GET"
    },
    {
        url: "/api/v1/lobby/"+lobbyCodeMock+"/changes",
        responseText: { 
            "lobbyExists": true,
            "lobbyContentChanged": false,
        },
        type: "GET"
    }
];


function newDomMock( resolve, reject_ ) {
    JSDOM.fromFile("./../templates/lobby.html").then(domJSDOM => {
                
        //console.log("done Setting up lobby DOM");
        dom = domJSDOM;
        window = dom.window;
        dom.reconfigure({  url: "http://mockAddress/game/"+lobbyCodeMock });
        window = Object.assign(window, { innerWidth: 500 });
        Object.defineProperty(window, 'innerWidth', {writable: true, configurable: true, value: 200})
        $ = require('jquery')(window);
        mockjax = mockjaxFunc($, window);
        resolve(dom);
    }).catch((e)=>{
        console.warn("ERROR COULD NOT FIND HTML TEMPLATE!!!!!!!!!!!!!!!!");
        resolve(false);
    });
}

QUnit.module( "Lobby Logic module", {
    beforeEach: function() {
        //console.log("Setting up DOM beforeEach and mock reset (LobbyLogic module)");
        return new Promise( newDomMock );
    },

    afterEach: function () {
        console.log("afterEach mock reset (Lobby Logic)");
        
        if ( $.mockjax.unfiredHandlers().length){
            console.log("unused ajax mocks (Lobby Logic):");
            console.log($.mockjax.unfiredHandlers());
        }
        if ( $.mockjax.unmockedAjaxCalls().length ) {
            console.log("unmockedAjaxCalls ajax mocks (Lobby Logic):");
            console.log($.mockjax.unmockedAjaxCalls());
        }
        // console.log("mockedAjaxCalls ajax mocks:");
        // console.log($.mockjax.mockedAjaxCalls())
        $.mockjax.clear();
    }
});

/*Uwaga! 
    nodejs niepoinformuje mnie że nie ma jakiejśzmiennej wewnątrz testów, tylko uzna że test się nie powiódł.
*/

test('Lobby creation test with player', function(assert){
    assert.timeout( 5000 );
    //console.log("test with mockjax player");
    var done = assert.async(); 
    
    mockjax(playerMock());
    $.mockjaxSettings.logger = null;

    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        
        assert.equal(typeof data === 'object', true, 'Successfully recived message from Lobby Init function.');
        done();
    });
    
});

test('Lobby singleton test with player', function(assert){
    assert.timeout( 5000 );
    //console.log("Lobby singleton test with player");
    var done = assert.async(); 
    
    mockjax(playerMock());
    $.mockjaxSettings.logger = null;

    var lm = LobbyModule({$:$, window:window});
    var areSameObjects = (obj1, obj2) =>  assert.equal(obj1 == obj2, true, 'Objects are the same.');

    var ll = lm.LobbyLogic(false, function(data) {
        
        areSameObjects(data, lm.LobbyLogic.singleton);
        areSameObjects(data, lm.LobbyLogic());
        areSameObjects(data, lm.getInstance());

        done();
    });
    
});

test('Lobby creation test with host', function(assert){
    assert.timeout( 5000 );
    //console.log("test with mockjax host");
    var done = assert.async(); 
    
    mockjax(hostMock());
    $.mockjaxSettings.logger = null;
    
    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        
        assert.equal(typeof data == 'object', true, 'Successfully recived message from Lobby Init function.');
        done();
    });
    
});

test('Lobby copy lobbycode test with host', function(assert){
    assert.timeout( 5000 );
    //console.log("Lobby copy lobbycode test with host");
    var done = assert.async(); 
    
    mockjax(hostMock());
    $.mockjaxSettings.logger = null;
    
    window.document.execCommand = (command, ui, value) => {
        const selection = window.getSelection();

        if ( !selection)
            return true;

        const node = selection.anchorNode;
        if ( !selection.anchorNode )
            return true;

        switch (command) {
          case 'copy':
            if (node.textContent) {
              return true
            } else { // Text node
              return false
            }
        }
    }

    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        var testValue = data.copyTextToClipboard(lobbyCodeMock);
        assert.equal(testValue, true, 'Successfully copied lobby code (but clipboard and focus not supported in nodejs?).');
        done();
    });
    
});

test('Lobby startgame test with host', function(assert){
    assert.timeout( 5000 );
    //console.log("Lobby startgame test with host");
    var done = assert.async();
    
    var ajaxMocks = [...hostMock(),
        // {
        //     url: "/api/v1/game/"+lobbyCodeMock+"/ping",
        //     responseText: { },
        //     type: "POST"
        // },
        {
            url: "/api/v1/game/"+lobbyCodeMock+"/tasks/current",
            responseText: { 
            },
            type: "GET"
        }
    ]

    mockjax(ajaxMocks);

    $.mockjaxSettings.logger = null;

    // cytoscape = require('cytoscape')(window, window.document);
    
    var cytoscopeMock = function(data) {
        //this is a mock object
        
        var usedMathodes = [];
        var node = function(){var self = {}; self.style = function(){usedMathodes.push('node.style')};  return self;}
        var nodess = [node(), node(), node()];
        nodess.ungrabify = function(e){ usedMathodes.push('nodes.ungrabify')};
        var elementss = [];
        elementss.remove = function(e){ usedMathodes.push('elements.remove')};
        return {
            boxSelectionEnabled: function(bool){ usedMathodes.push('boxSelectionEnabled')},
            panningEnabled: function(bool){ usedMathodes.push('panningEnabled')},
            elements:  function(){usedMathodes.push('elements');return elementss;},
            add: function(data) { usedMathodes.push('add'); },
            nodes: function() { usedMathodes.push('nodes'); return nodess},
            usedMathodes: usedMathodes,
        }   
    }

    class ResizeObserver {
        observe() {
            // do nothing
        }
        unobserve() {
            // do nothing
        }
    }

    window.cytoscape = cytoscopeMock;
    window.ResizeObserver = ResizeObserver;

    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        
        data.startGame((gameObj) => {
            assert.equal(gameObj.gameExist, true, 'Game has started.');
            done();
        });
        
    });
    
});

test('Lobby lobbySetupAfterChange test with host', function(assert){
    assert.timeout( 5000 );
    //console.log("Lobby lobbySetupAfterChange test with host");
    var done = assert.async();

    mockjax(hostMock());

    $.mockjaxSettings.logger = null;

    var mockLobbResponse = {
        exists:false,
        possiblyJoinedFromEndGame : true
    }

    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        
        assert.equal(data.lobbySetupAfterChange(mockLobbResponse), "prevented from displaying info", 'Game leave test.');
        assert.equal(data.lobbySetupAfterChange(mockLobbResponse), "displaying info", 'Game leave test 2.');
        done();
    });
    
});

test('Lobby leave test with host', function(assert){
    assert.timeout( 5000 );
    //console.log("Lobby leave test with host");
    var done = assert.async();
    
    var ajaxMocks = [...hostMock(),
        {
            url: "/api/v1/lobby/"+lobbyCodeMock+"/leave",
            responseText: { },
            type: "POST"
        }
    ]

    mockjax(ajaxMocks);
    $.mockjaxSettings.logger = null;
    //console.log("mockjax(ajaxMocks);");

    var assertionFunction = (href) => {
        console.log("assertionFunction");
        assert.equal("/lobby", href, 'Game leave test 3.');
        done();
    }

    var windowLocationMock = (function(){

        var self = {};
        self.wasDone = false;
        self.mytimeout = setTimeout(function(){
            assertionFunction(self.href);
            self.wasDone = true;
        },4000);
        self.href = "http://mockAddress/game/"+lobbyCodeMock;
        self.replace = function(data){
            self.href = data;

            if ( !self.wasDone ) {
                clearTimeout(self.mytimeout);
                assertionFunction(self.href);
                self.wasDone = true;
            }
        }
        return self;
    })

    
    //console.log("LobbyModule");
    //console.log(typeof LobbyModule);
    var ll = LobbyModule({$:$, window:window}).getInstance(false, function(data) {
        //console.log("before delete window.location");
        delete window.location;
        window.location = windowLocationMock();
        //console.log("after windowLocationMock");
        $("#btnSendleave").trigger('click');
        
        //console.log("after trigger");
    });
    
});

//niżej był test dom'a

// test('Lobby test with dom', function(assert){
//     console.log("test with dom");
//     var done = assert.async(); //musze ogarnąć jak mockować ajaxa tutaj

//     //console.log($('body').html());
//     console.log($('#lobbyCode').html());
    
//     //var cos = LobbyLogic.getInstance();

//     setTimeout(function() {
//         assert.ok(true, 'test a1');
//         done();
//     }, 100);
// });