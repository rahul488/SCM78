/**
 * 
 */

const toggleSidebar=()=>{
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
	}else{
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

const search=()=>{
	
	let query=$("#search-input").val();
	
	if(query == ""){
		$(".search-result").hide();
	}
	else{
		//search
		console.log(query); 
		//sending request to server
		let url=`http://localhost:8080/search/${query}`;
		fetch(url).then((response) => {
			return response.json();
		}).then((data)=>{
			console.log(data);

			let text=`<div class='list-group'>`;
			
			data.forEach((contact) => {
				text+=`<a href='/users/contact/${contact.id}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
			});

			text+=`</div>`;
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
	}
};


//for payment integration

const paymentStart=() =>{

	console.log("paymet Started");

	let amount=$("#payment_field").val();
	console.log(amount);

	if(amount == '' || amount == null){
		swal("Oops!", "Amount is required", "error");
		return ;
	}

	//Ajax to send request to server
	 $.ajax({
		//sending request to backend
		url:'/users/create_order',
		data:JSON.stringify(
			{
				amount:amount,
				info:'order-request'
			}
		),
		contentType:'application/json',
		type:'POST',
		dataType:'json',

		success:function (response) {
			console.log('success');
			console.log(response);
			if(response.status == "created"){
				let options={
					key:'rzp_test_EcvsAXSTCmnlAX',
					amount:response.amount,
					currency:'INR',
					name:'SCM',
					description:"Donation",
					order_id:response.id,

					handler:function(response){
						console.log(response.razorpay_payment_id);

						console.log(response.razorpay_order_id);

						console.log(response.razorpay_signature);

						console.log('paymet Successfull');

						swal("Good job!", "Payment Successfull!", "success");
					},
					"prefill": {
						"name": "",
						"email": "",
						"contact": ""
					},
					"notes": {
						"address": "Razorpay Corporate Office"
					},
					"theme": {
						"color": "#3399cc"
					},
				};
				let rzp=new Razorpay(options);

				rzp.on('payment.failed', function (response){
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					
					swal("Opps!", "Payment failed", "error");
			});

				rzp.open();
			}
		},
		error:function(error){
			console.log(error);
			alert('payment unsuccessfull');
		}

	 });
	

};