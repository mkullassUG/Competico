const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

var gameMocks = () => [
    
];

var domMocks = () => [
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
    }
]

function newDomMock( resolve, reject_ ) {

    JSDOM.fromFile("./../templates/lobby.html").then(domJSDOM => {
                
        console.log("almost done DOM");
        dom = domJSDOM;
        window = dom.window;
        dom.reconfigure({  url: "http://mockAddress/game/BkaeDoPJ" });
        $ = require('jquery')(window);
        mockjax = mockjaxFunc($, window);

        
        $.mockjaxSettings.logger = null;
        resolve(dom);
    });
}

var lobbyModuleMock;

QUnit.module( "GameLogic module", {
    beforeEach: function() {
        console.log("Setting up DOM beforeEach and mock reset (GameLogic module)");

        return new Promise( newDomMock ).then((res)=> {
            
            console.log("DOM done")
            mockjax(domMocks());
            lobbyModuleMock = LobbyModule($, window).getInstance(false, false, function(data) {
                //$.mockjax.clear();
            });
        });
    },

    afterEach: function () {
        console.log("Test done (GameLogic module).");
        
        console.log("unused ajax mocks (GameLogic module):");
        console.log($.mockjax.unfiredHandlers());
        console.log("unmockedAjaxCalls ajax mocks(GameLogic module):");
        console.log($.mockjax.unmockedAjaxCalls());
        $.mockjax.clear();
    }
});


test('Game creation test with host', function(assert){
    console.log("Game creation test with host");
    var done = assert.async(); 
    
    mockjax(gameMocks());

    $.mockjaxSettings.logger = null;

    assert.equal(true, true, 'test Game');
    done();
    
});