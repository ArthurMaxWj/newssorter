import Button from './Button'

function HomeButton(props) {

  return <Button label={<i class="fa-solid fa-arrow-left" />} btnClass="round-button" action={() => props.setPage("home")} />
}

export default HomeButton
