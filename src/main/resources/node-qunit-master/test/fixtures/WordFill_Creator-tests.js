const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

QUnit.module( "TaskCreator Logic module", {
    before: function() {
        console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/taskCreator.html").then(domJSDOM => {
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


test('Creating WordFill_Creator test', function(assert){
    
    var done = assert.async(); 
    
    dom.reconfigure({  url: "http://mockAddress/tasks/import/global/#WordFill" });

    $.mockjaxSettings.logger = null;

    var dependencies = {
        "TaskCreatorVariant" : TaskCreatorVariant
    }
    
    var tc = WordFill_Creator({}, false, $, window, dependencies, function(data) {
        
        assert.equal(data, "success", 'Succesfuly recived message from WordFill_Creator Init function.');
        done();
    });
    
});