import { useState } from 'react'
import { API_URL } from '../api/api'

function NotificationForm({
  token,
  onNotificationCreated,
}) {
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [channel, setChannel] = useState('EMAIL')
  const [recipient, setRecipient] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSubmitting(true)

    try {
      const response = await fetch(
        `${API_URL}/api/notifications`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            title,
            content,
            channel,
            recipient,
          }),
        }
      )

      if (!response.ok) {
        const data = await response.json()

        throw new Error(
          data.message || 'Failed to create notification'
        )
      }

      const notification = await response.json()

      onNotificationCreated(notification)

      setTitle('')
      setContent('')
      setRecipient('')
    } catch (error) {
      setError(error.message)
    } finally {
      setSubmitting(false)
    }
  }

  const getRecipientPlaceholder = () => {
    switch (channel) {
      case 'EMAIL':
        return 'user@example.com'
      case 'SMS':
        return '+528112345678'
      case 'PUSH':
        return 'device-token-abc123'
      default:
        return ''
    }
  }

  return (
    <section className="create-card">
      <div className="create-card-header">
        <div>
          <h2>Create Notification</h2>
          <p>
            Choose a channel and send a new notification.
          </p>
        </div>

        <span
          className={`channel-badge ${channel.toLowerCase()}`}
        >
          {channel}
        </span>
      </div>

      <form
        className="notification-form"
        onSubmit={handleSubmit}
      >
        <div className="form-group full-width">
          <label htmlFor="title">Title</label>

          <input
            id="title"
            type="text"
            placeholder="Notification title"
            value={title}
            onChange={(event) =>
              setTitle(event.target.value)
            }
            required
          />
        </div>

        <div className="form-group full-width">
          <label htmlFor="content">Content</label>

          <textarea
            id="content"
            placeholder="Write your message..."
            value={content}
            onChange={(event) =>
              setContent(event.target.value)
            }
            maxLength={
              channel === 'SMS' ? 160 : undefined
            }
            required
          />

          {channel === 'SMS' && (
            <span className="character-count">
              {content.length}/160 characters
            </span>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="channel">Channel</label>

          <select
            id="channel"
            value={channel}
            onChange={(event) => {
              setChannel(event.target.value)
              setRecipient('')
            }}
          >
            <option value="EMAIL">Email</option>
            <option value="SMS">SMS</option>
            <option value="PUSH">Push</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="recipient">Recipient</label>

          <input
            id="recipient"
            type="text"
            value={recipient}
            placeholder={getRecipientPlaceholder()}
            onChange={(event) =>
              setRecipient(event.target.value)
            }
            required
          />
        </div>

        {error && (
          <div className="error-message full-width">
            {error}
          </div>
        )}

        <div className="form-actions full-width">
          <button
            className="primary-button create-button"
            type="submit"
            disabled={submitting}
          >
            {submitting
              ? 'Sending...'
              : 'Create Notification'}
          </button>
        </div>
      </form>
    </section>
  )
}

export default NotificationForm