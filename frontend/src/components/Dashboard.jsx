import { useEffect, useState } from 'react'
import { API_URL } from '../api/api'
import NotificationForm from './NotificationForm'
import EditNotificationForm from './EditNotificationForm'

function Dashboard({ token, onLogout }) {
  const [notifications, setNotifications] = useState([])
  const [editingNotification, setEditingNotification] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await fetch(
          `${API_URL}/api/notifications`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )

        // If the JWT is invalid or expired, close the session.
        if (response.status === 401) {
          onLogout()
          return
        }

        if (!response.ok) {
          throw new Error('Failed to load notifications')
        }

        const data = await response.json()
        setNotifications(data)
      } catch (error) {
        setError(error.message)
      } finally {
        setLoading(false)
      }
    }

    fetchNotifications()
  }, [token, onLogout])

  const handleNotificationCreated = (notification) => {
    setNotifications((currentNotifications) => [
      ...currentNotifications,
      notification,
    ])
  }

  const handleDelete = async (id) => {
    try {
      setError('')

      const response = await fetch(
        `${API_URL}/api/notifications/${id}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )

      if (response.status === 401) {
        onLogout()
        return
      }

      if (!response.ok) {
        throw new Error('Failed to delete notification')
      }

      setNotifications((currentNotifications) =>
        currentNotifications.filter(
          (notification) => notification.id !== id
        )
      )
    } catch (error) {
      setError(error.message)
    }
  }

  const handleNotificationUpdated = (updatedNotification) => {
    setNotifications((currentNotifications) =>
      currentNotifications.map((notification) =>
        notification.id === updatedNotification.id
          ? updatedNotification
          : notification
      )
    )

    setEditingNotification(null)
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div>
          <p className="eyebrow">Dashboard</p>

          <h2>My Notifications</h2>

          <p className="dashboard-subtitle">
            Create, manage and review your notifications.
          </p>
        </div>

        <button
          className="secondary-button logout-button"
          onClick={onLogout}
        >
          Logout
        </button>
      </header>

      <NotificationForm
        token={token}
        onNotificationCreated={handleNotificationCreated}
      />

      <section className="notifications-section">
        <div className="section-header">
          <div>
            <h2>Notifications</h2>

            <p>
              {notifications.length}{' '}
              {notifications.length === 1
                ? 'notification'
                : 'notifications'}
            </p>
          </div>
        </div>

        {loading && (
          <div className="status-card">
            <p>Loading notifications...</p>
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          notifications.length === 0 && (
            <div className="empty-state">
              <div className="empty-icon">N</div>

              <h3>No notifications yet</h3>

              <p>
                Create your first notification using the form above.
              </p>
            </div>
          )}

        <div className="notifications-grid">
          {notifications.map((notification) => (
            <article
              className="notification-card"
              key={notification.id}
            >
              {editingNotification?.id === notification.id ? (
                <EditNotificationForm
                  notification={notification}
                  token={token}
                  onNotificationUpdated={
                    handleNotificationUpdated
                  }
                  onCancel={() =>
                    setEditingNotification(null)
                  }
                />
              ) : (
                <>
                  <div className="notification-card-header">
                    <h3>{notification.title}</h3>

                    <span
                      className={`channel-badge ${notification.channel.toLowerCase()}`}
                    >
                      {notification.channel}
                    </span>
                  </div>

                  <p className="notification-content">
                    {notification.content}
                  </p>

                  <div className="notification-meta">
                    <span>Recipient</span>

                    <strong>
                      {notification.recipient}
                    </strong>
                  </div>

                  <div className="notification-actions">
                    <button
                      className="secondary-button"
                      onClick={() =>
                        setEditingNotification(notification)
                      }
                    >
                      Edit
                    </button>

                    <button
                      className="danger-button"
                      onClick={() =>
                        handleDelete(notification.id)
                      }
                    >
                      Delete
                    </button>
                  </div>
                </>
              )}
            </article>
          ))}
        </div>
      </section>
    </div>
  )
}

export default Dashboard