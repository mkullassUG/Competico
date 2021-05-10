var path = require('path'),
    _ = require('underscore');

var istanbul,
    collector,
    options = {
        dir: 'coverage',
        reporters: ['lcov', 'json']
    };

try {
    istanbul = require('istanbul');
} catch (e) {
    // Ignore
}

exports.setup = function(opts) {
    collector = new istanbul.Collector();

    _.extend(options, opts);
    options.dir = path.resolve(options.dir);
};

exports.add = function(coverage) {
    if (collector && coverage) collector.add(coverage);
};

exports.get = function() {
    var summaries;
    if (collector) {
        summaries = [];
        collector.files().forEach(function(file) {
            summaries.push(istanbul.utils.summarizeFileCoverage(collector.fileCoverageFor(file)));
        });
        return istanbul.utils.mergeSummaryObjects.apply(null, summaries);
    }
};

exports.report = function() {
    var Report, reports;

    if (collector) {
        Report = istanbul.Report;

        reports = options.reporters.map(function (report) {
            return Report.create(report, options);
        });

        reports.forEach(function(rep) {
            rep.writeReport(collector, true);
        });
    }
};

exports.instrument = function(options) {
    var matcher, instrumenter;

    matcher = function (file) {
        var files = options.coverage.files;
        if (files) {
            files = Array.isArray(files) ? files : [files];
            return files.some(function(f) {
                if (typeof f === 'string') return file.indexOf(f) === 0;
                else throw new Error("invalid entry in options.coverage.files: " + typeof f);
            });
        } else {
            return file === options.code.path;
        }
    }
    instrumenter = new istanbul.Instrumenter();


    /*
        Ponieważ istanbul jest już nie wspierany, nie będzie on poprawnie rozumiał kodu ECMAScript 2018 (możliwe że też ECMAScript 2017?)

        Nie moge używać np : Array/Object spread operator
        
        Alternatywą jest zapoznanie się z modułem nyc i zaimplementowanie jej.
    */
    istanbul.hook.hookRequire(matcher, instrumenter.instrumentSync.bind(instrumenter));
};

if (!istanbul) {
    _.each(exports, function(fn, name) {
        exports[name] = function() {
            console.error('\nModule "istanbul" is not installed.'.red);
            process.exit(1);
        };
    });
}
