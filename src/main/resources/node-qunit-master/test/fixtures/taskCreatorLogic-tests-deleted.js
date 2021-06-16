const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

QUnit.module( "TaskCreator Logic module", {
    before: function() {
        console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/task-manager-lektor.html").then(domJSDOM => {
                console.log("done Setting up taskCreator DOM");

                dom = domJSDOM;
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


test('Creating TaskCreatorLogic test with mockjax', function(assert){
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
                "roles": ["Player","ACTUATOR_ADMIN","TASK_DATA_ADMIN"]
            },
            type: "GET"
        }
    ]);

    dom.reconfigure({  url: "http://mockAddress/tasks/import/global" });

    $.mockjaxSettings.logger = null;

    var dependencies = {
        "TaskCreatorCore" : TaskCreatorCore,
        "TaskGameCore" : TaskGameCore,
        $mock: $,
        windowMock: window
        // "NavbarLogic" : NavbarLogic,
    }
    
    var tcl = TaskCreatorModule($).getInstance(false, false, dependencies, function(data) {
        
        assert.equal(data, true, 'Succesfuly recived message from TaskCreatorLogic Init function.');
        done();
    });
    
});