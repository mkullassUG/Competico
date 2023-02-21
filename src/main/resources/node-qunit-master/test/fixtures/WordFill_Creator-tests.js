const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

QUnit.module( "TaskCreator Logic module", {
    before: function() {
        //console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/task-manager-lektor.html").then(domJSDOM => {
                console.log("done Setting up taskCreator DOM");

                dom = domJSDOM;
                window = dom.window;
                $ = require('jquery')(window);
                mockjax = mockjaxFunc($, window);

                resolve(dom);
            }).catch((e)=>{
                console.warn("ERROR COULD NOT FIND HTML TEMPLATE!!!!!!!!!!!!!!!!");
                resolve(false);
            });
        });
    }
});


test('Creating WordFill_Creator test', function(assert){
    
    var done = assert.async(); 
    
    dom.reconfigure({  url: "http://mockAddress/tasks/import/global/#WordFill" });

    $.mockjaxSettings.logger = null;

    var dependencies = {
        "TaskCreatorVariant" : TaskCreatorVariant,
        // $mock: $,
        // windowMock: window
    }
    
    var tc = WordFill_Creator({}, false, $, window, dependencies, function(data) {
        
        assert.equal(data, true, 'Succesfuly recived message from WordFill_Creator Init function.');
        done();
    });
    
});