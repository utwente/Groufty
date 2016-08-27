const gulp = require('gulp');
const del = require('del');
const concat = require('gulp-concat');
const uglify = require('gulp-uglify');
const sourceMaps = require('gulp-sourcemaps');
const typescript = require('gulp-typescript');
const less = require('gulp-less');
const tscConfig = require('./tsconfig.json');
const gulpTypings = require("gulp-typings");

// Simple task to cleanup static files
gulp.task('clean', function () {
    return del('../resources/static/**/*',
        {force: true});
});

// Task to copy all Angular 2 related dist files
gulp.task('copy:libs', ['clean'], function() {
    gulp.src(["node_modules/es6-shim/es6-shim.min.js", "node_modules/es6-shim/es6-shim.map"])
        .pipe(gulp.dest('../resources/static/js/libs'));
    gulp.src(["node_modules/pdfjs-dist/build/pdf.worker.js"])
        .pipe(gulp.dest('../resources/static/js/libs'));
    gulp.src(["node_modules/@angular/**/*"])
        .pipe(gulp.dest('../resources/static/js/libs/@angular'));
    gulp.src(["node_modules/angular2-in-memory-web-api/**/*"])
        .pipe(gulp.dest('../resources/static/js/libs/angular2-in-memory-web-api'));
    gulp.src(["node_modules/rxjs/**/*"])
        .pipe(gulp.dest('../resources/static/js/libs/rxjs'));
    return gulp.src([
            "node_modules/marked/marked.min.js",

            "node_modules/moment/min/moment.min.js",
            "node_modules/moment/min/locales.min.js",

            "node_modules/pdfjs-dist/build/pdf.js",
            "node_modules/pdfjs-dist/web/compatibility.js",
            "node_modules/pdfjs-dist/web/pdf_viewer.js",

            "node_modules/zone.js/dist/zone.js",
            "node_modules/reflect-metadata/Reflect.js",

            "node_modules/systemjs/dist/system.src.js",
            "systemjs.config.js"
        ])
        .pipe(concat('libs.js'))
        //.pipe(uglify({mangle: false}))
        .pipe(gulp.dest('../resources/static/js/libs'))
});

// Copy the needed fonts
gulp.task('copy:fonts', ['clean'], function() {
    return gulp.src([
            "node_modules/font-awesome/fonts/**/*"
        ])
        .pipe(gulp.dest('../resources/static/fonts'))
});

// Copy the static assets
gulp.task('copy:assets', ['clean'], function() {
    return gulp.src(['static/**/*'], { base : './' })
        .pipe(gulp.dest('../resources'))
});

// Install the typings file.
gulp.task("install:typings", ['clean'], function(){
    return gulp.src("./typings.json")
        .pipe(gulpTypings());
});

// Compile less files to css
gulp.task('compile:less', ['clean'], function () {
    return gulp.src('less/groufty.less')
        .pipe(less({
            paths: [ 'node_modules/bootstrap/less' ]
        }))
        .pipe(gulp.dest('../resources/static/css'));
});

// TypeScript compile
gulp.task('compile:ts', ['clean', 'install:typings'], function () {
    tscConfig.typescript = require("typescript");
    return gulp
        .src('ts/**/*.ts')
        .pipe(sourceMaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourceMaps.write('.'))
        .pipe(gulp.dest('../resources/static/js'));
});

gulp.task('build', ['install:typings', 'compile:ts', 'compile:less', 'copy:libs', 'copy:assets', 'copy:fonts']);
gulp.task('compile', ['compile:ts', 'compile:less']);
gulp.task('default', ['build']);