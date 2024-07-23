const stripe = Stripe('pk_test_51P5sSZRr6KpfdnMoZ1cQf7MM8ujIJGwfp9Hh9BrbnFsdjsr9Qgas0ut2sJwZkkj64ycRcJACYPsFaiASgpPUXSOR000mo42iGK');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
  stripe.redirectToCheckout({
    sessionId: sessionId
  })
});

