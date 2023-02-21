const jsdom = require("jsdom");
const { JSDOM } = jsdom;
var $, window, dom;


function newDomMock( resolve, reject_ ) {
    JSDOM.fromFile("./../templates/task-manager-lektor.html").then(domJSDOM => {
        //console.log("done Setting up task manager DOM");
        dom = domJSDOM;
        window = dom.window;
        dom.reconfigure({url: "http://mockAddress/lecturer/taskmanager/"});
        window = Object.assign(window, { innerWidth: 500 });
        Object.defineProperty(window, 'innerWidth', {writable: true, configurable: true, value: 200})
        $ = require('jquery')(window);
        resolve(dom);
    });
}

QUnit.module( "Dual-list module", {
    beforeEach: function() {
        //console.log("Setting up DOM beforeEach and mock reset (Dual-list module)");
        return new Promise( newDomMock );
    },

    afterEach: function () {
        //console.log("afterEach mock reset");
    }
});


test('Dual-list creation test', function(assert){
    var done = assert.async(); 
    
    var deps = {
        $: $,
        window: window
    }
    
    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        
        assert.equal(typeof data === 'object', true, 'Successfully recived message from Dual-list Init function.');
        done();
    });
});

test('Dual-list creation with tasksets test', function(assert){
    var done = assert.async(); 
    
    var deps = {
        $: $,
        window: window
    }
    
    DualListModule(deps).DualListLogic({selector:"#dualList", tasksets:[1,2,3,4]}, false, function(data) {
        
        assert.equal(data.inputOptions.length , 4, 'Successfully created dual list with 4 tasksets.');
        done();
    });
});

test('Dual-list insert test', function(assert) {
    var done = assert.async(); 
    
    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.refresh();
        dl.insertOptions([1,2,3,4,5]);
        var options = $("#duallist-non-selected").children();
        assert.equal(options.length, 5, 'Expected the number of options to be 5.');
        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
    
});

test('Dual-list refresh test', function(assert) {
    var done = assert.async(); 
    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.insertOptions([1,2,3,4,5]);
        dl.refresh();
        var options = $("#duallist-non-selected").children();
        assert.equal(options.length, 0, 'Expected the number of options to be 0.');
        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
    
});

test('Dual-list filter tests', function(assert) {
    var done = assert.async(); 
    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.refresh()
        dl.insertOptions(["zestaw1","zestaw2","zestaw3","zestaw4","zestaw5","zestaw6","zestaw7","taskset8","taskset9","taskset10"]);
        dl.insertIntoInputFilter("taskset");
        $(".input-text-filter").trigger('click');
        var options = $("#duallist-non-selected").children();
        assert.equal(options.length, 3, 'Expected the number of filtered options to be 3.');

        
        dl.insertIntoInputFilter(" ");
        $(".input-text-filter").trigger('click');
        var options = $("#duallist-non-selected").children();
        assert.equal(options.length, 10, 'Expected the number of filtered options to be 10.');

        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
});

test('Dual-list move options between lists tests', function(assert) {
    var done = assert.async(); 

    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.refresh()
        dl.insertOptions(["zestaw1","zestaw2","zestaw3","zestaw4","zestaw5","zestaw6","zestaw7","taskset8","taskset9","taskset10"]);
        $("#duallist-non-selected ").val(["zestaw3","taskset8"]).change();

        $("#dualList > div:nth-child(1) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.input-move-one").trigger('click');
        var options = $("#duallist-selected").children();
        assert.equal(options.length, 2, 'Expected the number of options to be 2.');

        $("#duallist-selected").val(["taskset8"]).change();
        $("#dualList > div:nth-child(2) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.output-move-one").trigger('click');
        options = $("#duallist-selected").children();
        assert.equal(options.length, 1, 'Expected the number of options to be 1.');

        $("#dualList > div:nth-child(1) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.input-move-multi").trigger('click');
        options = $("#duallist-non-selected").children();
        assert.equal(options.length, 0, 'Expected the number of options to be 0.');
        options = $("#duallist-selected").children();
        assert.equal(options.length, 10, 'Expected the number of options to be 10.');

        $("#dualList > div:nth-child(2) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.output-move-multi").trigger('click');
        options = $("#duallist-non-selected").children();
        assert.equal(options.length, 10, 'Expected the number of options to be 10.');
        options = $("#duallist-selected").children();
        assert.equal(options.length, 0, 'Expected the number of options to be 0.');

        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
});

test('Dual-list get output list test', function(assert) {
    var done = assert.async(); 
    
    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.refresh()
        dl.insertOptions(["zestaw1","zestaw2","zestaw3","zestaw4","zestaw5","zestaw6","zestaw7","taskset8","taskset9","taskset10"]);
        $("#duallist-non-selected ").val(["zestaw3","taskset8"]).change();

        $("#dualList > div:nth-child(1) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.input-move-one").trigger('click');
        var output = dl.getOutput();

        assert.equal(output.length, 2, 'Expected the number of output values to be 2.');

        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
});

test('Dual-list check if task exists tests', function(assert) {
    var done = assert.async(); 
    
    var deps = {
        $: $,
        window: window
    }

    var assertFunction = (dl) => {
        dl.refresh()
        dl.insertOptions(["zestaw1","zestaw2","zestaw3","zestaw4","zestaw5","zestaw6","zestaw7","taskset8","taskset9","taskset10"]);
        $("#duallist-non-selected ").val(["zestaw3","taskset8"]).change();

        $("#dualList > div:nth-child(1) > div:nth-child(4) > button.btn.btn-lg.btn-outline-secondary.w-50.input-move-one").trigger('click');
        
        var res1 = dl.tasksetAlreadyExists("zestaw3");
        var res2 = dl.tasksetAlreadyExists("zestaw2");
        var res3 = dl.tasksetAlreadyExists("zestaw11");

        assert.equal(res1, true, 'Expected to find taskset with specific name inside the lists.');
        assert.equal(res2, true, 'Expected to find taskset with specific name inside the lists.');
        assert.equal(res3, false, 'Expected to not find taskset with specific name inside the lists.');

        done();
    }

    DualListModule(deps).DualListLogic({selector:"#dualList"}, false, function(data) {
        assertFunction(data);
    });
});