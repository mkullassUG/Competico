
(function() {
    'use strict';

    var i, l,
        parts = document.location.search.match( /testFiles=([^&]+)/ ),
        ignore = document.location.search.match( /ignoreFiles=([^&]+)/ ),
        testFiles = [
              // This will become: <script src='test-code.js'></script>
            'code',
            'tasks',
            'gameLogic',
            'lobbyLogic'
        ];

    if ( parts && parts[1] ) {
        try {
            var inputFiles = JSON.parse( decodeURIComponent(parts[ 1 ]) ) || null;
            if (inputFiles && inputFiles.length && inputFiles[0] !== 'all') {
                testFiles = inputFiles;
            }
        } catch(err) {
            console.warn('\n WARNING: Unable to parse the test modules you wanted:', err);
            testFiles = [];
        }
    }

    if ( ignore && ignore[1] ) {
        try {
            var ignoreFiles = JSON.parse( decodeURIComponent(ignore[ 1 ]) ) || null;
            if (ignoreFiles && ignoreFiles.length) {
                testFiles = testFiles.filter(function(file) {
                    return ignoreFiles.indexOf(file) === -1;
                });
            }
        } catch(err) {
            console.warn('\n WARNING: Unable to parse the test modules you wanted to ignore:', err);
        }
    }

	for ( i=0, l = testFiles.length; i<l; i++ ) {
		document.write( '<script src="test-' + testFiles[ i ] + '.js"></script>' );
	}

}());
