import { useState } from 'react'
import { API_URL } from '../api/api'

function EditNotificationForm({
  notification,
  token,
  onNotificationUpdated,
  onCancel,
}) {
  const [title, setTitle] = useState(notification.title)
  const [content, setContent] = useState(notification.content)
  const [channel, setChannel] = useState(notification.channel)
  const [recipient, setRecipient] = useState(notification.recipient)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSubmitting(true)

    try {
      const response = await fetch(
        `${API_URL}/api/notifications/${notification.id}`,
        {
          method: 'PUT',
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
          data.message || 'Failed to update notification'
        )
      }

      const updatedNotification = await response.json()

      onNotificationUpdated(updatedNotification)
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
    <form
      className="edit-notification-form"
      onSubmit={handleSubmit}
    >
      <div className="edit-form-header">
        <div>
          <p className="eyebrow">Editing</p>
          <h3>Edit Notification</h3>
        </div>

        <span
          className={`channel-badge ${channel.toLowerCase()}`}
        >
          {channel}
        </span>
      </div>

      <div className="form-group">
        <label htmlFor={`edit-title-${notification.id}`}>
          Title
        </label>

        <input
          id={`edit-title-${notification.id}`}
          type="text"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor={`edit-content-${notification.id}`}>
          Content
        </label>

        <textarea
          id={`edit-content-${notification.id}`}
          value={content}
          onChange={(event) => setContent(event.target.value)}
          maxLength={channel === 'SMS' ? 160 : undefined}
          required
        />

        {channel === 'SMS' && (
          <span className="character-count">
            {content.length}/160 characters
          </span>
        )}
      </div>

      <div className="form-group">
        <label htmlFor={`edit-channel-${notification.id}`}>
          Channel
        </label>

        <select
          id={`edit-channel-${notification.id}`}
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
        <label htmlFor={`edit-recipient-${notification.id}`}>
          Recipient
        </label>

        <input
          id={`edit-recipient-${notification.id}`}
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
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="edit-actions">
        <button
          className="secondary-button"
          type="button"
          onClick={onCancel}
          disabled={submitting}
        >
          Cancel
        </button>

        <button
          className="primary-button save-button"
          type="submit"
          disabled={submitting}
        >
          {submitting ? 'Saving...' : 'Save Changes'}
        </button>
      </div>
    </form>
  )
}

export default EditNotificationForm