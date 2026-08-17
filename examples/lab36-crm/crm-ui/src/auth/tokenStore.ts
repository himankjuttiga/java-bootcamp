/**
 * In-memory token only — never localStorage/sessionStorage.
 *
 * The point is not that memory is unreadable: script running in our origin can read anything
 * the page can reach. The point is persistence. A token in Web Storage is still there tomorrow,
 * so one XSS hit yields a credential that keeps working long after the payload is gone. A module
 * variable dies with the tab, which bounds the blast radius to a single page view.
 *
 * Accepted cost: a refresh signs the user out, because there is nothing to rehydrate from.
 */
let accessToken: string | null = null

export const tokenStore = {
  get(): string | null {
    return accessToken
  },
  set(token: string | null) {
    accessToken = token
  },
  clear() {
    accessToken = null
  },
}
