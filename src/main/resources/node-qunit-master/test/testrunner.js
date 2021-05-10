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

// chain.add('base testrunner', function() {
//     tr.run({
//         code: fixtures + '/testrunner-code.js',
//         tests: fixtures + '/testrunner-tests.js',
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 4,
//             assertions: 7,
//             failed: 2,
//             passed: 5
//         };
//         a.equal(err, null, 'no errors');
//         a.ok(res.runtime > 0, 'Date was modified');
//         delete res.runtime;
//         delete res.coverage;
//         a.deepEqual(stat, res, 'base testrunner test');
//         chain.next();
//     });
// });

// chain.add('attach code to global', function() {
//     tr.run({
//         code: fixtures + '/child-code-global.js',
//         tests: fixtures + '/child-tests-global.js',
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 1,
//             assertions: 2,
//             failed: 0,
//             passed: 2
//         };

//         delete res.runtime;
//         delete res.coverage;
//         a.equal(err, null, 'no errors');
//         a.deepEqual(stat, res, 'attaching code to global works');
//         chain.next();
//     });
// });

// chain.add('attach deps to global', function() {
//     tr.run({
//         deps: fixtures + '/child-code-global.js',
//         code: fixtures + '/testrunner-code.js',
//         tests: fixtures + '/child-tests-global.js',
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 1,
//             assertions: 2,
//             failed: 0,
//             passed: 2
//         };

//         delete res.runtime;
//         delete res.coverage;
//         a.equal(err, null, 'no errors');
//         a.deepEqual(stat, res, 'attaching dependencies to global works');
//         chain.next();
//     });
// });

// chain.add('attach code to a namespace', function() {
//     tr.run({
//         code: {
//             path: fixtures + '/child-code-namespace.js',
//             namespace: 'testns'
//         },
//         tests: fixtures + '/child-tests-namespace.js',
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 1,
//             assertions: 3,
//             failed: 0,
//             passed: 3
//         };

//         delete res.runtime;
//         delete res.coverage;
//         a.equal(err, null, 'no errors');
//         a.deepEqual(stat, res, 'attaching code to specified namespace works');
//         chain.next();
//     });
// });

// chain.add('async testing logs', function() {
//     tr.run({
//         code: fixtures + '/async-code.js',
//         tests: fixtures + '/async-test.js',
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 4,
//             assertions: 6,
//             failed: 0,
//             passed: 6
//         };

//         delete res.runtime;
//         delete res.coverage;
//         a.equal(err, null, 'no errors');
//         a.deepEqual(stat, res, 'async code testing works');
//         chain.next();
//     });
// });

// chain.add('uncaught exception', function() {
//     tr.run({
//         code: fixtures + '/uncaught-exception-code.js',
//         tests: fixtures + '/uncaught-exception-test.js',
//     }, function(err) {
//         a.ok(err instanceof Error, 'error was forwarded');
//         chain.next();
//     });
// });

// chain.add('infinite loop', function() {
//     tr.run({
//         code: fixtures + '/infinite-loop-code.js',
//         tests: fixtures + '/infinite-loop-test.js',
//     }, function(err) {
//         a.ok(err instanceof Error, 'error was forwarded');
//         chain.next();
//     });
// });

// chain.add('coverage', function() {
//     tr.options.coverage = true;
//     tr.run({
//         code: fixtures + '/coverage-code.js',
//         tests: fixtures + '/coverage-test.js'
//     }, function(err, res) {
//         var stat = {
//             files: 1,
//             tests: 2,
//             assertions: 3,
//             failed: 0,
//             passed: 3,
//             coverage: {
//                 files: 1,
//                 statements: { covered: 8, total: 9 },
//                 branches: { covered: 0, total: 0 },
//                 functions: { covered: 4, total: 5 },
//                 lines: { covered: 8, total: 9 }
//             }
//         };
//         delete res.runtime;
//         a.equal(err, null, 'no errors');
//         //a.deepEqual(stat, res, 'coverage code testing works');
//         tr.options.coverage = false;
//         chain.next();
//     });
// });

// chain.add('coverage-multiple', function() {
//     tr.options.coverage = true;
//     tr.run({
//         code: fixtures + '/coverage-multiple-code.js',
//         tests: fixtures + '/coverage-test.js',
//         // coverage: {
//         //     files: [
//         //         fixtures + '/coverage-code.js',
//         //         fixtures + '/coverage-multiple-code.js'
//         //     ],
//         // },
//     }, function(err, res) {
//         //zmiany bo nie przechodziło
//         /*
//             statements: { covered: 7, total: 8 },
//             branches: { covered: 0, total: 0 },
//             functions: { covered: 3, total: 4 },
//             lines: { covered: 7, total: 8 }
//         */
//         var stat = {
//             files: 1,
//             tests: 2,
//             assertions: 3,
//             failed: 0,
//             passed: 3,
//             coverage: {
//                 files: 1,
//                 statements: { covered: 0, total: 0 },
//                 branches: { covered: 0, total: 0 },
//                 functions: { covered: 0, total: 0 },
//                 lines: { covered: 0, total: 0 }
//             }
//         };
//         delete res.runtime;
//         a.equal(err, null, 'no errors');
//         //a.deepEqual(stat, res, 'coverage multiple code testing works');
//         tr.options.coverage = false;
//         chain.next();
//     });
// });

/*chce dodać własne funkcje do kodu do przetestowania i program ma jakiś problem z tym na końcu*/

/*nie ogarnaim czem uw indexie tego reportu co tworzy ustawia tylko ostatnie testy z chaina*/

// chain.add('ghillieCode', function() {
//     console.log("------ghillieCode start-----")
//     tr.options.coverage = true;
//     tr.run([{
//         code: __dirname + "/ghillieCode/Ghillie-code.js",
//         tests: __dirname + "/ghillieCode/Ghillie-tests.js"
//     },
//      {
//          code: __dirname + "/ghillieCode/Ghillie-code2.js",
//          tests: __dirname + "/ghillieCode/Ghillie-tests2.js"
//      }
// ], function(err, res) {
//         // console.log("------ghillieCode results-----")
//         // console.log(err)
//         // console.log(res)

//         //tutaj moge określać ile % coverage chce żeby przepuszczało

//         // var stat = {
//         //     files: 2,
//         //     assertions: 4,
//         //     failed: 1,
//         //     passed: 3,
//         //     runtime: 4,
//         //     tests: 2,
//         //     coverage: {
//         //         files: 2,
//         //         statements: { covered: 5, total: 10 },
//         //         branches: { covered: 0, total: 0 },
//         //         functions: { covered: 0, total: 5 },
//         //         lines: { covered: 5, total: 10 }
//         //     }
//         // };
//         // delete res.runtime;
//         a.equal(err, null, 'no errors');
//         // a.deepEqual(stat, res, 'coverage code testing works');
//         tr.options.coverage = false;
//         chain.next();
//     });
// });

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
    testing: true
    tr.setup({
        log: {
            assertions: true,
            summary: true,
            globalSummary: true,
        }
    });
    tr.options.coverage = true;
    tr.run([
        // {
        //     code: fixtures_code + "/myTestObj.js",
        //     tests: fixtures + "/myTestObj-tests.js"
        // },
        {
            code: fixtures_code + "/AccountValidation.js",
            tests: fixtures + "/AccountValidation-tests.js"
        },
        {
            code: fixtures_code + "/lobbyLogic.js",
            tests: fixtures + "/lobbyLogic-tests.js"
        },
        {
            deps: [
                fixtures_code + '/tasks/taskCreatorCore.js',
                fixtures_code + '/tasks/taskGameCore.js',
                // fixtures_code + '/navbar.js',
            ],
            code: fixtures_code + "/taskCreatorLogic.js",
            tests: fixtures + "/taskCreatorLogic-tests.js"
        },
        {
            deps: [
                fixtures_code + '/tasks/taskGameCore.js'
            ],
            code: fixtures_code + "/tasks/taskCreatorCore.js",
            tests: fixtures + "/taskCreatorCore-tests.js"
        },
        {
            deps: [
                fixtures_code + '/tasks/taskCreatorVariants/TaskCreatorVariant.js'
            ],
            code: fixtures_code + "/tasks/taskCreatorVariants/WordFill_Creator.js",
            tests: fixtures + "/WordFill_Creator-tests.js"
        }
    ],  function(err, res) {
        // console.log("------ghillieCode results-----")
        // console.log("err")
        // console.log(err)
        // console.log("res")
        // console.log(res)
        
        a.equal(err, null, 'no errors');
        // a.deepEqual(stat, res, 'coverage code testing works');
        tr.options.coverage = false;
        chain.next();
    });
});

// if (generators.support) {
//     chain.add('generators', function() {
//         tr.run({
//             code: fixtures + '-example/generators-code.js',
//             tests: fixtures + '-example/generators-test.js'
//         }, function(err, res) {
//             var stat = {
//                 files: 1,
//                 tests: 1,
//                 assertions: 1,
//                 failed: 0,
//                 passed: 1
//             };
//             delete res.runtime;
//             delete res.coverage;
//             a.equal(err, null, 'no errors');
//             a.deepEqual(stat, res, 'coverage code testing works');
//             chain.next();
//         });
//     });
// }

chain.add(function() {
    console.log('\nAll tests ok.');
});

chain.start();
