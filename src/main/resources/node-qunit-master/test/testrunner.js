var a = require('assert'),
    chainer = require('chainer');

var tr = require('../lib/testrunner'),
    log = require('../lib/log'),
    generators = require('../lib/generators');

var fixtures = __dirname + '/fixtures',
    fixtures_code = __dirname + '../../../static/js',
    chain = chainer();

tr.options.log = {
    // log assertions overview
    // assertions: true,
    // log expected and actual values for failed tests
    // errors: true,
    // log tests overview
    //tests: true,
    // log summary
    // summary: true,
    // log global summary (all files)
    // globalSummary: true,
    // log coverage
    // coverage: true,
    // log global coverage (all files)
    // globalCoverage: true,
    // log currently testing code file
    testing: true
};

// reset log stats every time .next is called
chain.next = function() {
    log.reset();
    return chainer.prototype.next.apply(this, arguments);
};

chain.add('ghillieCode2', function() {
    console.log("------ghillieCode2 start-----")
    // log assertions overview
    // assertions: true,
    // log expected and actual values for failed tests
    // errors: true,
    // log tests overview
    //tests: true,
    // log summary
    // summary: true,
    // log global summary (all files)
    // globalSummary: true,
    // log coverage
    // coverage: true,
    // log global coverage (all files)
    // globalCoverage: true,
    // log currently testing code file
    //testing: true
    tr.setup({
        log: {
            assertions: true,
            summary: true,
            globalSummary: true,
        }
    });
    tr.options.coverage = true;
    tr.options.maxBlockDuration = 1000;
    tr.run([
        {
            code: fixtures_code + "/account-validation.js",
            tests: fixtures + "/AccountValidation-tests.js"
        },
        {
            deps: [
                fixtures_code + '/game-logic.js',
                fixtures_code + '/tasks/task-game-core.js',
                // fixtures_code + '/cyto/cytoscape.js',
            ],
            code: fixtures_code + "/lobby-logic.js",
            tests: fixtures + "/lobbyLogic-tests.js"
        },
        // {
        //     deps: [
        //         fixtures_code + '/tasks/task-creator-core.js',
        //         fixtures_code + '/tasks/task-game-core.js',
        //         // fixtures_code + '/navbar.js',
        //     ],
        //     code: fixtures_code + "/task-creator-logic.js",
        //     tests: fixtures + "/taskCreatorLogic-tests.js"
        // },
        {
            deps: [
                fixtures_code + '/tasks/task-game-core.js'
            ],
            code: fixtures_code + "/tasks/task-creator-core.js",
            tests: fixtures + "/taskCreatorCore-tests.js"
        },
        {
            deps: [
                fixtures_code + '/tasks/taskCreatorVariants/TaskCreatorVariant.js'
            ],
            code: fixtures_code + "/tasks/taskCreatorVariants/WordFill_Creator.js",
            tests: fixtures + "/WordFill_Creator-tests.js"
        },
        {
            // deps: [
            //     fixtures_code + '/navbar.js'
            // ],
            code: fixtures_code + "/register.js",
            tests: fixtures + "/register-tests.js"
        },
        {
            deps: [
                fixtures_code + "/lobby-logic.js",
                fixtures_code + "/tasks/taskCreatorVariants/WordFill_Creator.js",
                fixtures_code + '/tasks/taskCreatorVariants/TaskCreatorVariant.js'
            ],
            code: fixtures_code + '/game-logic.js',
            tests: fixtures + "/gameLogic-tests.js"
        },
        {
            code: fixtures_code + '/dual-list-logic.js',
            tests: fixtures + "/dual-list-logic-tests.js"
        },
    ],  function(err, res) {
        // console.log("------ghillieCode results-----")
        // console.log("err")
        //console.log("Done")
        //console.log(err)
        // console.log("res")
        // console.log(res)
        
        a.strictEqual(err, null, 'no errors');
        //a.deepEqual(stat, res, 'coverage code testing works');
        tr.options.coverage = false;
        chain.next();
    });
});

chain.add(function() {
    console.log('\nAll tests ok.');
});

chain.start();
