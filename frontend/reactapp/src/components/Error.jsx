import Button from './Button'

function Error(props) {

  return <section id="error-page">
    <h2>Error has occured!</h2>
    <p>{props.text}</p>
    <Button label="Home" action={() => props.setPage("home")}/>
  </section>
}

export default Error
