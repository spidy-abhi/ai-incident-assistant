# Database Connection Pool Exhaustion

## Incident

The application is unable to obtain a database connection from the connection pool.

## Symptoms

- API requests become slow or fail.
- Database operations start timing out.
- Application logs contain connection timeout messages.
- The connection pool reaches its maximum number of active connections.

## Common Error

A typical error may look like:

`Connection is not available, request timed out after 30000ms`

## Possible Causes

Common causes include:

1. Database connections are not being released correctly.
2. Long-running database queries are holding connections.
3. Database response time has increased.
4. The application is receiving more traffic than expected.
5. The configured connection pool size is too small.

## Troubleshooting

1. Check application logs for connection timeout errors.
2. Check the number of active database connections.
3. Identify long-running database queries.
4. Verify that application code properly closes database resources.
5. Check database CPU and memory utilization.
6. Review recent traffic increases.
7. Review the connection pool configuration.

## Resolution

Depending on the root cause:

- Fix connection leaks.
- Optimize long-running queries.
- Increase database capacity if necessary.
- Adjust connection pool configuration carefully.
- Reduce unnecessary database calls.
- Investigate traffic spikes.

## Prevention

- Monitor active and idle database connections.
- Monitor connection pool utilization.
- Set appropriate connection timeout values.
- Detect connection leaks.
- Monitor long-running queries.