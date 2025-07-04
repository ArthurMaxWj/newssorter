import HomeButton from './HomeButton'

function Error(props) {

  return <section id="error-page">
    <h1>Error has occured!</h1>
    <p>{props.text}</p>
    <HomeButton setPage={props.setPage} />

  </section>
}

export default Error
