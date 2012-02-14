var citeAction = #{jsAction @Articles.cite(':id') /}
$('[action="cite"]').live('click', function() {
	var id = $(this).attr('article')
	var root = $(this).parents(".line")
	$.get(citeAction({id:id}), function(data) {
			root.replaceWith(data)
	})
});
