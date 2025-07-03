import Button from './Button'

function Error(props) {

  return <div>
    <h2>Error has occured!</h2>
    <p>{props.text}</p>
    <Button label="Home" action={() => props.setPage("home")}/>
  </div>
}

export default Error
