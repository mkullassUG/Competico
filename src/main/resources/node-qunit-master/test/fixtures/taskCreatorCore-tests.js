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


test('Creating TaskCreatorCore test', function (assert) {
    var done = assert.async(); 

    dom.reconfigure({  url: "http://mockAddress/tasks/import/global" });

    $.mockjaxSettings.logger = null;

    var dependencies = {
        "TaskGameCore" : TaskGameCore,
        // $mock: $,
        // windowMock: window
    }

    var tcc = TaskCreatorCore(false, $, window, dependencies, function(data) {
        
        assert.equal(data, "success", 'Succesfuly recived message from TaskCreatorCore Init function.');
        done();
    });
});