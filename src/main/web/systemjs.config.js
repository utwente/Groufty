/**
 * System configuration for Angular 2 samples
 * Adjust as necessary for your application needs.
 */
(function(global) {

    // map tells the System loader where to look for things
    var map = {
        'groufty':                    'js', // 'dist',
        '@angular':                   'js/libs/@angular',
        'angular2-in-memory-web-api': 'js/libs/angular2-in-memory-web-api',
        'rxjs':                       'js/libs/rxjs'
    };

    // packages tells the System loader how to load when no filename and/or no extension
    var packages = {
        'groufty':                    { main: 'groufty.js', defaultExtension: 'js' },
        'rxjs':                       { defaultExtension: 'js' },
        'angular2-in-memory-web-api': { defaultExtension: 'js' },
    };

    var ngPackageNames = [
        'common',
        'compiler',
        'core',
        'http',
        'platform-browser',
        'platform-browser-dynamic',
        'router',
        'router-deprecated',
        'upgrade',
    ];

    // Add package entries for angular packages
    ngPackageNames.forEach(function(pkgName) {

        // Bundled (~40 requests):
        packages['@angular/'+pkgName] = { main: pkgName + '.umd.js', defaultExtension: 'js' };

        // Individual files (~300 requests):
        //packages['@angular/'+pkgName] = { main: 'index.js', defaultExtension: 'js' };
    });

    var config = {
        map: map,
        packages: packages
    }

    System.config(config);
    System.import('groufty').catch(function(err){ console.error(err); });

})(this);