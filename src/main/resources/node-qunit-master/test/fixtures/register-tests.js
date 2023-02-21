const jsdom = require("jsdom");
const { JSDOM } = jsdom;
// const mockjaxFunc = require("jquery-mockjax");
var /*mockjax,*/ $, window, dom;

QUnit.module( "Register module", {
    before: function() {
        //console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/register.html").then(domJSDOM => {
                //console.log("done Setting up register DOM");

                dom = domJSDOM;
                window = dom.window;
                $ = require('jquery')(window);
                //mockjax = mockjaxFunc($, window);

                resolve(dom);
            }).catch((e)=>{
                console.warn("ERROR COULD NOT FIND HTML TEMPLATE!!!!!!!!!!!!!!!!");
                resolve(false);
            });
        });
    }
});


test('Creating RegisterModule test', function(assert){
    
    var done = assert.async(); 
    
    dom.reconfigure({  url: "http://mockAddress/register" });

    //$.mockjaxSettings.logger = null;

    var dependencies = {
        //"NavbarLogic" : NavbarLogic,
        // $mock: $,
        windowMock: window
    }
    
    var rm = RegisterModule($).getInstance( true, dependencies, function(data) {
        
        assert.equal(data, true, 'Succesfuly recived message from RegisterModule Init function.');
        done();
    });
    
});