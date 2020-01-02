(function(){
	XL.tree.init('#menu');
	XL.msg.alert('成功');
	XL.msg.alert('Default SmallPop');
	XL.msg.alert('<h4 class="spop-title">Success</h4>Iˈm a success SmallPop', 'success');
	XL.msg.alert('Warning SmallPop', 'warning');
	XL.msg.alert('<strong>Error Here!</strong>', 'error');
	XL.msg.alert({template: 'Position top left',position  : 'top-left',style: 'success'});
	XL.msg.alert({template: '3 seconds autoclose',autoclose: 3000});
	XL.msg.alert({template: 'All fields are required!',group: 'submit-satus',style: 'error'});
	XL.msg.alert('Nothing here...');
	XL.msg.alert({template: 'Your information has been submitted',group: 'submit-satus',style: 'success',autoclose: 2000});
	XL.msg.alert({template: 'Please, close me.',style:'warning',onOpen: function () {document.body.style.background = "#fff";},onClose: function() {document.body.style.background = "";
	XL.msg.alert({template: 'Thank you!',style: 'success',autoclose: 2000});}});
	XL.msg.alert('Got to <a href="#demo-defaults" data-spop="close">defaults</a>');
	XL.msg.alert('Defaults changed! See the others examples.');
})();