import { useState } from 'react'
import { API_URL } from '../api/api'

function Register({ onRegistrationSuccess }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    try {
      const response = await fetch(
        `${API_URL}/api/auth/register`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email,
            password,
          }),
        }
      )

      if (!response.ok) {
        const data = await response.json()

        throw new Error(
          data.message || 'Registration failed'
        )
      }

      onRegistrationSuccess()
    } catch (error) {
      setError(error.message)
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <div className="form-heading">
        <h2>Create account</h2>
        <p>Get started with Notification Manager.</p>
      </div>

      <div className="form-group">
        <label htmlFor="register-email">Email</label>

        <input
          id="register-email"
          type="email"
          placeholder="you@example.com"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="register-password">
          Password
        </label>

        <input
          id="register-password"
          type="password"
          placeholder="Minimum 8 characters"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          minLength={8}
          required
        />
      </div>

      {error && (
        <p className="error-message">{error}</p>
      )}

      <button className="primary-button" type="submit">
        Create account
      </button>
    </form>
  )
}

export default Register