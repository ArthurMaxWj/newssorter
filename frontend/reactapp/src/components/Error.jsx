import { useState } from 'react'

function Error(props) {

  return <div>
    <h2>Error has occured!</h2>
    <p>{props.text}</p>
  </div>
}

export default Error
