/**
 * Toggle cited/un-cited status of an Article.
 * Calls controller: Articles.cite
 * Usage: <span action="cite" article="<id here>">click!</span>
 * Request: AJAX GET
 */
var citeAction = #{jsAction @Articles.cite(':id') /}
$('[action="cite"]').live('click', function() {
	var id = $(this).attr('article')
	var root = $(this).parents(".line")
	$.get(citeAction({id:id}), function(data) {
			root.replaceWith(data)
	})
});

/**
 * Toggle starred/un-starred status of an Article.
 * Calls controller: Articles.star
 * Usage: <span action="star" article="<id here>">click!</span>
 * Request: AJAX GET
 */
var starAction = #{jsAction @Articles.star(':id') /}
$('[action="star"]').live('click', function() {
	var id = $(this).attr('article')
	var root = $(this).parents(".line")
	$.get(starAction({id:id}), function(data) {
			root.replaceWith(data)
	})
});