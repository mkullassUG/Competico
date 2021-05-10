//ghillie code:
const jsdom = require("jsdom");
const { JSDOM } = jsdom;
var myDom; 
var myWindow; 
var JQ = require('jquery');
var $;

QUnit.module( "Setting up DOM register", {
    before: function() {
        console.log("before Setting up DOM");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/register.html").then(dom => {
                
                console.log("done Setting up DOM");
                //console.log(dom.serialize())
                myDom = dom;
                myWindow = dom.window; //window location??
                $ = JQ(myWindow);

                resolve(dom);
            });
        });
    }
});
  
console.log("2");
test('test with dom', function(assert){
    console.log("test with dom");
    var done = assert.async(); //musze ogarnąć jak mockować ajaxa tutaj

    //console.log($('body').html());
    console.log($('#emailfeedback').html());
    
    var cos = myObj($);

    console.log(cos.getEmailfeedback());

    setTimeout(function() {
        assert.ok(true, 'test a1');
        done();
    }, 100);
});

QUnit.module("Module A1", {
    before: function() {
        console.log("before Setting up DOM dashboard");
        return new Promise( function( resolve, reject_ ) {

            JSDOM.fromFile("./../templates/dashboard.html").then(dom => {
                
                console.log("done Setting up DOM");
                //console.log(dom.serialize())
                myDom = dom;
                myWindow = dom.window;
                $ = JQ(myWindow);

                resolve(dom);
            });
        });
    },
    beforeEach: function() {
      // prepare something before each test
      console.log("beforeEach testing");
    },
    afterEach: function() {
      // clean up after each test
      console.log("afterEach testing");
    },
    after: function() {
      // clean up once after all tests are done
      console.log("after testing");
    },


    //te niżej ponoć usunięte w najnowszym Qunicie
    setup: function () {
        
        // do some initial stuff before every test for this module
        console.log("starting testing");

    },
    teardown: function () {
        // do some stuff after every test for this module
        console.log("finished testing");
    }
});

test('async', function(assert){
    console.log("test async");
    var done = assert.async();

    
    setTimeout(function() {
        assert.ok(true, 'test a1');
        done();
    }, 100);
});

test("Invalid data validation for registration", function (assert) {
    console.log("test my hello obj");
    var cos = myObj($);

    assert.equal(cos.getHello(), "hello", "We expect value to be hello");
});