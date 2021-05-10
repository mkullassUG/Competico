const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

/*Uwaga!
    jeśli zmienie nazwe template.html to nie wiem co się stanie bez reject_ w JSDOM dom

*/
QUnit.module( "Lobby Logic module", {
    before: function() {
        console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/lobby.html").then(domJSDOM => {
                
                console.log("done Setting up lobby DOM");

                dom = domJSDOM;
                /*
                    Zmiana window.location.href na mock url
                */
                
                window = dom.window;

                $ = require('jquery')(window);
                mockjax = mockjaxFunc($, window);

                resolve(dom);
            });
        });
    },
    beforeEach: function() {
        console.log("beforeEach mock reset");
    }
});

/*Uwaga! 
    nodejs niepoinformuje mnie że nie ma jakiejśzmiennej wewnątrz testów, tylko uzna że test się nie powiódł.
*/

test('Lobby test with mockjax', function(assert){
    console.log("test with mockjax");
    var done = assert.async(); 
    
    mockjax([
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
            url: "/api/v1/lobby/BkaeDoPJ",
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
        }
    ]);

    dom.reconfigure({  url: "http://mockAddress/game/BkaeDoPJ" });

    $.mockjaxSettings.logger = null;
    
    var ll = LobbyLogic.getInstance(false, $, window, function(data) {
        
        assert.equal(data, "success", 'Succesfuly recived message from Lobby Init function.');
        done();
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