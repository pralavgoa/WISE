alert("This is from the survey values handler");

$(document).ready(function(){
	
	var div_class_to_exclude = ".repeating_question";
	var div_id = "#content";
	
//	$(div_id).children(":input:not("+div_class_to_exclude+")").each(function(){
//		alert($(this).attr("name"));
//	});
	
//	$("input[type='radio']:checked :not()").each(function() {
//			alert($(this).val());
//	});

	var div_index = 1000;
	$(".repeat_item_save").click(function () {
		//Get the project instance name
		var project_name = "";
		$(this).siblings("input").each(function(){
			project_name = $(this).val();
		});
		
		//Get contents of this div as html
		//create a string which has a div, with div id, and then put all the content inside that div
		var add_html = "<h3 class='repeating_item_instance'>"+project_name+"</h3>";
		 add_html += "<div id='"+project_name+"'>";		
		$(this).parent().children("div").each(function(){
			
			//change the ids of the input elements
			
			$(this).children("input").each(function() {
				var input_element_id = $(this).attr("id");
				$(this).attr("id","repeat_"+input_element_id);
			});
			
			
			add_html += "<div>";		
			
			add_html += $(this).html();
			add_html += "</div>";
		});
		add_html += "</div>";
		
		//append this div to the repeating questions parent div
		$("div.repeating_question").append(add_html);
		
	});	
	
	//minimize the repeating item_instance
	
	//toggle the repeating item instance
	$(".repeating_item_instance").live('click', function(){
		$(this).next("div").toggle("slow");
	});
	
	
});