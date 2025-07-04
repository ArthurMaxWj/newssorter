import Button from './Button'

function RefreshButton() {
  const refreshPage = () => {
    window.location.reload()
  }

  return <Button label={<i class="fa-solid fa-arrows-rotate"></i>} btnClass="round-button" action={refreshPage} />
}

export default RefreshButton
