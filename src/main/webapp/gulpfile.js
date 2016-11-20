// generated on 2016-06-28 using generator-webapp 2.1.0
const gulp = require('gulp');
const gulpLoadPlugins = require('gulp-load-plugins');
const browserSync = require('browser-sync');
const del = require('del');
const wiredep = require('wiredep').stream;
const minifyInline = require('gulp-minify-inline');
const transport = require('gulp-seajs-transport');
const revCollector = require('gulp-rev-collector');
const revReplace = require('gulp-rev-replace');
const filter = require('gulp-filter');
const es = require('event-stream');
const $ = gulpLoadPlugins();
const reload = browserSync.reload;

const staticSrcPath = "resource/src/";
const staticDistPath = "resource/dist/";

gulp.task('styles', () => {
	return gulp.src(staticSrcPath + 'styles/*.css')
			.pipe($.plumber())
			.pipe($.sourcemaps.init())
			.pipe($.autoprefixer({browsers: ['> 1%', 'last 2 versions', 'Firefox ESR']}))
			.pipe($.rev())
			.pipe($.cssnano({safe: true, autoprefixer: false}))

			.pipe($.sourcemaps.write('../sourcemaps/css'))
			.pipe(gulp.dest('.tmp/styles'))
			//.pipe($.copy(staticDistPath + "styles"))

			.pipe($.rev.manifest())
			.pipe(gulp.dest('rev/css'))
			.pipe(reload({stream: true}));
});

gulp.task('scripts', () => {
	return gulp.src([
				staticSrcPath + 'scripts/**/*.js',
				"!" + staticSrcPath + 'scripts/tsc/*.js',
				"!" + staticSrcPath + 'scripts/polyfill/*.js'
			], {base: "./resource/src/scripts/"})
			.pipe($.plumber())
			.pipe($.sourcemaps.init())
			//.pipe($.babel())
			.pipe(transport())
			.pipe($.rev())
			.pipe($.uglify())
			.pipe($.sourcemaps.write('../sourcemaps/js'))
			.pipe(gulp.dest('.tmp/scripts'))
			// .pipe(gulp.dest(staticDistPath + "scripts"))
			.pipe($.rev.manifest())
			.pipe($.jsonminify())
			.pipe(gulp.dest('rev/js'))
			.pipe(reload({stream: true}));
});

function lint(files, options) {
	return gulp.src(files)
			.pipe(reload({stream: true, once: true}))
			.pipe($.eslint(options))
			.pipe($.eslint.format())
			.pipe($.if(!browserSync.active, $.eslint.failAfterError()));
}

gulp.task('lint', () => {
	return lint(staticSrcPath + 'scripts/**/*.js', {
		fix: true
	})
	//.pipe(gulp.dest(staticDistPath +'scripts'));
});


gulp.task('html', ['styles', 'scripts'], () => {
	var js_css_filter = filter(["**/*.js", "**/*.css"], {restore: true});
	var jsp_filter = filter('**/*.jsp', {restore: true});

	return gulp.src([
				'WEB-INF/view/common/core/css-dev.jsp',
				'WEB-INF/view/common/core/js-dev.jsp'
			])
			.pipe(minifyInline())
			.pipe($.useref({
				searchPath: ['.tmp', staticSrcPath, "resource", '.'],
				transformPath: function (filePath) {
					var fs = require('fs');
					var obj = JSON.parse(fs.readFileSync('rev/js/rev-manifest.json', 'utf8'));
					return filePath.replace('${root}/', '')
							.replace('${ctx}/', '')
							.replace('${res}/', '')
							.replace("common/ui.js",obj["common/ui.js"] || "common/ui.js")
							.replace("common/appView.js",obj["common/appView.js"] || "common/appView.js")
							.replace("common/util.js",obj["common/util.js"] || "common/util.js")
							.replace("main.js",obj["main.js"] || "main.js")
				}
			}))

			.pipe($.if('*.js', $.uglify()))
			.pipe(
					$.if(
							'*.css',
							$.cssnano({safe: true, autoprefixer: false})
					)
			)
			.pipe(js_css_filter)
			.pipe($.rev())
			.pipe(js_css_filter.restore)

			.pipe(jsp_filter)
			.pipe($.replace(/<link.*?href=["']([^'"]+)["'].*?path=(\$\{[^}]+})[^>]*>/ig, '<link rel="stylesheet" href="$2/$1"/>'))
			.pipe($.replace(/<script\s*src=["']([^'"]+)["']\s*path=(\$\{[^}]+})>\s*<\/\s*script>/ig, "<script src='$2/$1'></script>"))
			.pipe($.htmlmin({collapseWhitespace: true}))
			.pipe(jsp_filter.restore)

			.pipe(gulp.dest(".tmp"))
			.pipe($.rev.manifest())
			.pipe(gulp.dest(".tmp"));
});

gulp.task('htmlVersion', ['html'], () => {
	var jsp_filter = filter('**/*.jsp', {restore: true});
	return gulp.src(['.tmp/**/*.json','.tmp/**/*.jsp'])
			.pipe(revCollector({
				replaceReved: true
			}))
			.pipe(jsp_filter)
			//.pipe($.replace(/<link.*?href=["']([^'"]+)["'].*?path=(\$\{[^}]+})[^>]*>/ig, '<link rel="stylesheet" href="$2/$1"/>'))
			//.pipe($.replace(/<script\s*src=["']([^'"]+)["']\s*path=(\$\{[^}]+})>\s*<\/\s*script>/ig, "<script src='$2/$1'></script>"))
			.pipe(jsp_filter.restore)
			.pipe(gulp.dest(".tmp"));
});

gulp.task('images', () => {
	return gulp.src(staticSrcPath + 'images/**/*')
			.pipe($.cache($.imagemin({
				progressive: true,
				interlaced: true,
				svgoPlugins: [{cleanupIDs: false}]
			})))
			.pipe(gulp.dest(staticDistPath + 'images'));
});

gulp.task('fonts', () => {
	return gulp.src(require('main-bower-files')('**/*.{eot,svg,ttf,woff,woff2}', function (err) {
			})
			.concat(staticSrcPath + 'fonts/**/*'))
			.pipe(gulp.dest('.tmp/fonts'))
			.pipe(gulp.dest(staticDistPath + 'fonts'));
});

gulp.task('extras', () => {
	es.merge(
			gulp.src(".tmp/css-dev.jsp").pipe($.rename("css.jsp")),
			gulp.src(".tmp/js-dev.jsp").pipe($.rename("js.jsp")),
			gulp.src(".tmp/tsc-dev.jsp").pipe($.rename("tsc.jsp"))
	).pipe(gulp.dest("WEB-INF/view/common/core"))
	return gulp.src([
				'.tmp/**/*.*',
				'!.tmp/*.jsp',
				'!.tmp/*.json'
			])
			.pipe(gulp.dest(staticDistPath));
});

gulp.task('clean', del.bind(null, ['.tmp', staticDistPath]));

gulp.task('serve', ['styles', 'scripts', 'fonts'], () => {
	/*	browserSync({
	 notify: false,
	 port: 9000,
	 server: {
	 baseDir: ['.tmp', staticSrcPath]
	 }
	 });*/

	gulp.watch([
		'.tmp/**/*'
	],['extras']);

	gulp.watch(staticSrcPath + 'styles/**/*.css', ['styles']);
	gulp.watch(staticSrcPath + 'scripts/**/*.js', ['scripts']);
	gulp.watch(staticSrcPath + 'fonts/**/*', ['fonts']);
	gulp.watch([
		'WEB-INF/view/common/core/css-dev.jsp',
		'WEB-INF/view/common/core/js-dev.jsp'
	],['htmlVersion']);
	gulp.watch('bower.json', ['wiredep', 'fonts']);
});

gulp.task('serve:dist', () => {
	browserSync({
		notify: false,
		port: 9000,
		server: {
			baseDir: [staticDistPath]
		}
	});
});

// inject bower components
gulp.task('wiredep', () => {
	gulp.src('app/*.html')
			.pipe(wiredep({
				exclude: ['bootstrap.js'],
				ignorePath: /^(\.\.\/)*\.\./
			}))
			.pipe(gulp.dest('app'));
});

gulp.task('build', ['lint', 'htmlVersion', 'images', 'fonts'], () => {
	gulp.start('extras');
	return gulp.src(staticDistPath + '**/*').pipe($.size({title: 'build', gzip: true}));
});

gulp.task('default', ['clean'], () => {
	gulp.start('build');
});
